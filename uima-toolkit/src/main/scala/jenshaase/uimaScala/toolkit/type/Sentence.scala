/* First created by JCasGen Tue Apr 12 17:01:07 CEST 2011*/
package jenshaase.uimaScala.toolkit.type

import org.apache.uima.jcas.{JCas, JCasRegistry}
import org.apache.uima.jcas.cas.TOP_Type

import org.apache.uima.jcas.tcas.Annotation


/* Updated by JCasGen Tue Apr 12 17:01:07 CEST 2011
 * Xml Source ./uima-toolkit/src/main/resources/desc/type/toolkit-typ-system.xml
 * @generated */
class Sentence extends Annotation {

  /* @generated */
  def getTypeIndexID(): Integer = {
    Sentence.typeIndexID
  }

  /* Never called.  Disable default constructor
   * @generated */
  protected def this() = {}

  /* Initernal - constructor used by generator
   * @generated */
  def this(addr: Integer, type: TOP_TYPE) = {
    this(addr, type)
    readObject()
  }

  /* @generated */
  def this(jcas: JCas) = {
    this(jcas)
    readObject()
  }

  /* @generated */
  def this(jcas: JCas, begin: Integer, end: Integer) = {
    this(jcas)
    setBegin(begin)
    setEnd(end)
    readObject()
  }

  /* <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   * @generated modifiable */
  private def readObject(): Unit = {}


}

/* Updated by JCasGen Tue Apr 12 17:01:07 CEST 2011
 * Xml Source ./uima-toolkit/src/main/resources/desc/type/toolkit-typ-system.xml
 * @generated */
object Sentence  {

  /* A simple sentence annotation
   * @generated
   * @ordered */
  val typeIndexID = JCasRegistry.register(classOf[Sentence])

  /* @generated
   * @ordered */
  val type = typeIndexID


}

