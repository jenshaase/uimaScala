/* First created by JCasGenTue Apr 12 17:01:07 CEST 2011*/
package jenshaase.uimaScala.toolkit.type

import org.apache.uima.jcas.{JCas, JCasRegistry}
import org.apache.uima.cas.impl.{CASImpl, FSGenerator, TypeImpl}
import org.apache.uima.cas.{FeatureStructure, Type}

import org.apache.uima.jcas.tcas.Annotation_Type


/* Updated by JCasGen Tue Apr 12 17:01:07 CEST 2011
 * @generated */
class Token_Type extends org.apache.uima.jcas.tcas.Annotation_Type {

  /* @generated */
  def getFSGenerator() = {
    fsGenerator
  }

  /* @generated */
  val fsGenerator = 
    new FSGenerator() {
      def createFS(addr: Integer, cas: CASImpl): FeatureStructure = {
        if (Token_Type.this.useExistingInstance) {
          var fs = Token_Type.this.jcas.getJfsFromCaddr(addr)
        
          if (null == fs) {
            fs = new Token(addr, Token_Type.this)
            Token_Type.this.jcas.putJfsFromCaddr(addr, fs)
          }
          fs
        } else new Token(addr, Token_Type.this)
      }

    }

  /* initialize variables to correspond with Cas Type and Features
   * @generated */
  def this(jcas: JCas, casType: Type) = {
    this(jcas, casType)
    casImpl.getFSClassRegistry().addGeneratorForType(this.casType.asInstanceOf[TypeImpl], getFSGenerator())
  }


}

/* A simple token annotation
 * Updated by JCasGen Tue Apr 12 17:01:07 CEST 2011
 * @generated */
object Token_Type  {

  /* @generated */
  val typeIndexID = Token.typeIndexID

  /* @generated
   * @modifiable */
  val featOkTst = JCasRegistry.getFeatOkTst("jenshaase.uimaScala.toolkit.type.Token")


}

