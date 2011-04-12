/* First created by JCasGenTue Apr 12 17:01:07 CEST 2011*/
package jenshaase.uimaScala.toolkit.type

import org.apache.uima.jcas.{JCas, JCasRegistry}
import org.apache.uima.cas.impl.{CASImpl, FSGenerator, TypeImpl}
import org.apache.uima.cas.{FeatureStructure, Type}

import org.apache.uima.jcas.tcas.Annotation_Type


/* Updated by JCasGen Tue Apr 12 17:01:07 CEST 2011
 * @generated */
class Sentence_Type extends org.apache.uima.jcas.tcas.Annotation_Type {

  /* @generated */
  def getFSGenerator() = {
    fsGenerator
  }

  /* @generated */
  val fsGenerator = 
    new FSGenerator() {
      def createFS(addr: Integer, cas: CASImpl): FeatureStructure = {
        if (Sentence_Type.this.useExistingInstance) {
          var fs = Sentence_Type.this.jcas.getJfsFromCaddr(addr)
        
          if (null == fs) {
            fs = new Sentence(addr, Sentence_Type.this)
            Sentence_Type.this.jcas.putJfsFromCaddr(addr, fs)
          }
          fs
        } else new Sentence(addr, Sentence_Type.this)
      }

    }

  /* initialize variables to correspond with Cas Type and Features
   * @generated */
  def this(jcas: JCas, casType: Type) = {
    this(jcas, casType)
    casImpl.getFSClassRegistry().addGeneratorForType(this.casType.asInstanceOf[TypeImpl], getFSGenerator())
  }


}

/* A simple sentence annotation
 * Updated by JCasGen Tue Apr 12 17:01:07 CEST 2011
 * @generated */
object Sentence_Type  {

  /* @generated */
  val typeIndexID = Sentence.typeIndexID

  /* @generated
   * @modifiable */
  val featOkTst = JCasRegistry.getFeatOkTst("jenshaase.uimaScala.toolkit.type.Sentence")


}

