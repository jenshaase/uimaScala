package com.github.jenshaase.uimascala.examples.ex1;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * @generated */
public class RoomNumber_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (RoomNumber_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = RoomNumber_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new RoomNumber(addr, RoomNumber_Type.this);
  			   RoomNumber_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new RoomNumber(addr, RoomNumber_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = RoomNumber.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.github.jenshaase.uimascala.examples.ex1.RoomNumber");
 
  /** @generated */
  final Feature casFeat_building;
  /** @generated */
  final int     casFeatCode_building;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getBuilding(int addr) {
        if (featOkTst && casFeat_building == null)
      jcas.throwFeatMissing("building", "com.github.jenshaase.uimascala.examples.ex1.RoomNumber");
    return ll_cas.ll_getStringValue(addr, casFeatCode_building);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBuilding(int addr, String v) {
        if (featOkTst && casFeat_building == null)
      jcas.throwFeatMissing("building", "com.github.jenshaase.uimascala.examples.ex1.RoomNumber");
    ll_cas.ll_setStringValue(addr, casFeatCode_building, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public RoomNumber_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_building = jcas.getRequiredFeatureDE(casType, "building", "uima.cas.String", featOkTst);
    casFeatCode_building  = (null == casFeat_building) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_building).getCode();

  }
}



    