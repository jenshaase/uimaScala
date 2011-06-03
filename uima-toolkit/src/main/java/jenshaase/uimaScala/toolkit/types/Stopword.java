

/* First created by JCasGen Fri Apr 29 11:18:30 CEST 2011 */
package jenshaase.uimaScala.toolkit.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** A stopword annotation
 * Updated by JCasGen Fri Apr 29 11:18:30 CEST 2011
 * XML source: ./uima-toolkit/src/main/resources/desc/types/toolkit-typ-system.xml
 * @generated */
public class Stopword extends Annotation {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(Stopword.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated  */
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Stopword() {}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Stopword(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Stopword(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Stopword(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {}
     
}

    