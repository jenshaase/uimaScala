

/* First created by JCasGen Sat Jul 26 10:15:09 CEST 2014 */
package com.github.jenshaase.uimascala.toolkit.description;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sat Jul 26 10:15:09 CEST 2014
 * XML source: /home/jens/dev/scala/uimaScala/uima-toolkit/src/main/resources/desc/types/BasicTypeDescription.xml
 * @generated */
public class DocumentAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentAnnotation.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected DocumentAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DocumentAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DocumentAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DocumentAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: name

  /** getter for name - gets 
   * @generated
   * @return value of the feature 
   */
  public String getName() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "com.github.jenshaase.uimascala.toolkit.description.DocumentAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setName(String v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "com.github.jenshaase.uimascala.toolkit.description.DocumentAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_name, v);}    
   
    
  //*--------------*
  //* Feature: source

  /** getter for source - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSource() {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "com.github.jenshaase.uimascala.toolkit.description.DocumentAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_source);}
    
  /** setter for source - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSource(String v) {
    if (DocumentAnnotation_Type.featOkTst && ((DocumentAnnotation_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "com.github.jenshaase.uimascala.toolkit.description.DocumentAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentAnnotation_Type)jcasType).casFeatCode_source, v);}    
  }

    