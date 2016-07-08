package com.github.jenshaase.uimascala.parser

import com.github.jenshaase.uimascala.core._
import com.github.jenshaase.uimascala.core.configuration._
import com.github.jenshaase.uimascala.typesystem._
import org.apache.uima.jcas.JCas
import scala.collection.JavaConversions._
import edu.stanford.nlp.parser.common.ParserGrammar
import java.io._
import java.util.zip.GZIPInputStream
import org.apache.uima.resource.DataResource
import org.apache.uima.resource.SharedResourceObject
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.trees.Tree
import org.apache.uima.jcas.tcas.Annotation
import org.apache.uima.jcas.cas.FSArray
import org.apache.uima.util.Level.WARNING

class StanfordParserGrammerResource extends SharedResourceObject {
  private var parser: ParserGrammar = _

  def load(data: DataResource) {
    parser = ParserGrammar.loadModel(data.getUri.toString)
  }

  def getParserGrammer = parser
}

object DependencyMode {
  val BASIC = "BASIC"
  val NON_COLLAPSED = "NON_COLLAPSED"
  val COLLAPSED = "COLLAPSED"
  val COLLAPSED_WITH_EXTRA = "COLLAPSED_WITH_EXTRA"
  val CC_PROPAGATED = "CC_PROPAGATED"
  val CC_PROPAGATED_NO_EXTRA = "CC_PROPAGATED_NO_EXTRA"
  val TREE = "TREE"
}

class StanfordParser extends SCasAnnotator_ImplBase {

  object model extends SharedResource[StanfordParserGrammerResource]("")
  object mode extends Parameter[String](DependencyMode.BASIC)
  object readPOS extends Parameter[Boolean](true) {
    override def mandatory_? = false
  }
  object createPOS extends Parameter[Boolean](false) {
    override def mandatory_? = false
  }

  def process(jcas: JCas) = {
    val parser = model.resource.getParserGrammer

    jcas.select[Sentence].foreach { sentence =>
      val tokens = jcas.selectCovered[Token](sentence).toVector

      val query = parser.parserQuery()
      query.parse(tokens.map(tokenToCoreLabel _))
      val parseTree = query.getBestParse()
      parseTree.setSpans()

      doCreateConstituentAnnotation(jcas, tokens, parseTree, None)
      doCreateDependencyAnnotation(jcas, parser, parseTree, tokens)
    }
  }

  def tokenToCoreLabel(token: Token): CoreLabel = {
    val word = new CoreLabel()
    word.setValue(token.getCoveredText)
    word.setOriginalText(token.getCoveredText)
    word.setWord(token.getCoveredText)
    word.setBeginPosition(token.getBegin)
    word.setEndPosition(token.getEnd)

    if (readPOS.is && token.getPos != null) {
      word.setTag(token.getPos.getName)
    }

    word
  }

  def doCreateConstituentAnnotation(jcas: JCas, tokens: Vector[Token], node: Tree, parent: Option[Annotation]): Annotation = {
    val nodeLabelValue = node.value()
    val source = tokens.get(node.getSpan().getSource)
    val target = tokens.get(node.getSpan().getTarget)

    if (node.isPhrasal) {
      val constituent = createConstituent(jcas, source.getBegin, target.getEnd, nodeLabelValue)
      parent.foreach { p => constituent.setParent(p) }

      val childAnnotations = node.
        getChildrenAsList().
        map(doCreateConstituentAnnotation(jcas, tokens, _, Some(constituent)))

      val children = childAnnotations.zipWithIndex.
        foldLeft(new FSArray(jcas, childAnnotations.size())) { case (fsArray, (ann, idx)) =>
          fsArray.set(idx, ann)
          fsArray
        }

      constituent.setChildren(children)
      add(constituent)
      constituent
    } else if (node.isPreTerminal) {
      val pos = createPOS(jcas, source.getBegin, target.getEnd, nodeLabelValue)
      val coveredToken = jcas.selectCovered[Token](pos)
      require(coveredToken.size == 1)
      val token = coveredToken.get(0)

      if (createPOS.is) {
        add(pos)
        token.setPos(pos)
      }

      parent.foreach { p =>
        token.setParent(p)
      }

      token
    } else {
      throw new Exception("Node must be either phrasal nor pre-terminal")
    }
  }

  def createConstituent(jcas: JCas, begin: Int, end: Int, constituentType: String) = {
    val c = new Constituent(jcas, begin, end)
    c.setConstituentType(constituentType)
    c
  }

  def createPOS(jcas: JCas, begin: Int, end: Int, name: String) = {
    val p = new POS(jcas, begin, end)
    p.setName(name)
    p
  }


  def doCreateDependencyAnnotation(jcas: JCas, parser: ParserGrammar, parseTree: Tree, tokens: Seq[Token]) {
    try {
      val gs = parser.getTLPParams().getGrammaticalStructure(
        parseTree,
        parser.treebankLanguagePack().punctuationWordRejectFilter(),
        parser.getTLPParams().typedDependencyHeadFinder()
      )

      val dependencies = mode.is match {
        case DependencyMode.BASIC => gs.typedDependencies()
        case DependencyMode.NON_COLLAPSED => gs.allTypedDependencies()
        case DependencyMode.COLLAPSED => gs.typedDependenciesCollapsed(false)
        case DependencyMode.COLLAPSED_WITH_EXTRA => gs.typedDependenciesCollapsed(true)
        case DependencyMode.CC_PROPAGATED => gs.typedDependenciesCCprocessed(true)
        case DependencyMode.CC_PROPAGATED_NO_EXTRA => gs.typedDependenciesCCprocessed(false)
        case DependencyMode.TREE => gs.typedDependenciesCollapsedTree()
        case _ => throw new Exception("DependencyMode not supported: " + mode.is)
      }

      dependencies.foreach { currTypedDep =>
        val govIndex = currTypedDep.gov().index();
        val depIndex = currTypedDep.dep().index();

        val dep = if (govIndex != 0) {
          val govToken = tokens(govIndex - 1)
          val depToken = tokens(depIndex - 1)

          val dep = new Dependency(jcas)
          dep.setDependencyType(currTypedDep.reln().toString());
          dep.setGovernor(govToken);
          dep.setDependent(depToken);
          dep.setBegin(dep.getDependent().getBegin());
          dep.setEnd(dep.getDependent().getEnd());
          dep.addToIndexes();
        } else {
          val depToken = tokens(depIndex - 1);
          
          val dep = new DependencyRoot(jcas);
          dep.setDependencyType(currTypedDep.reln().toString());
          dep.setGovernor(depToken);
          dep.setDependent(depToken);
          dep.setBegin(dep.getDependent().getBegin());
          dep.setEnd(dep.getDependent().getEnd());
          dep.addToIndexes();

          dep
        }
      }
    } catch {
      case e: UnsupportedOperationException =>
        getContext().getLogger().log(WARNING, "Current model does not seem to support dependencies.");
    }
  }
}
