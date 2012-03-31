

/* First created by JCasGen Sun Jan 22 13:52:21 CET 2012 */
package com.github.jenshaase.uimascala.toolkit.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Jan 22 13:52:21 CET 2012
 * XML source: /home/jens/programming/scala/uimaScala/uima-toolkit/src/main/resources/desc/types/com.github.jenshaase.uimascala.toolkit.description.BasicTypeDescription.xml
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

    