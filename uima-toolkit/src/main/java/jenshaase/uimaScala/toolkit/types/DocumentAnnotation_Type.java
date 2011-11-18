
/* First created by JCasGen Fri Nov 18 22:17:23 CET 2011 */
package jenshaase.uimaScala.toolkit.types;

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
 * Updated by JCasGen Fri Nov 18 22:17:23 CET 2011
 * @generated */
public class DocumentAnnotation_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DocumentAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DocumentAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DocumentAnnotation(addr, DocumentAnnotation_Type.this);
  			   DocumentAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DocumentAnnotation(addr, DocumentAnnotation_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = DocumentAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("jenshaase.uimaScala.toolkit.types.DocumentAnnotation");
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "jenshaase.uimaScala.toolkit.types.DocumentAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "jenshaase.uimaScala.toolkit.types.DocumentAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_source;
  /** @generated */
  final int     casFeatCode_source;
  /** @generated */ 
  public String getSource(int addr) {
        if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "jenshaase.uimaScala.toolkit.types.DocumentAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_source);
  }
  /** @generated */    
  public void setSource(int addr, String v) {
        if (featOkTst && casFeat_source == null)
      jcas.throwFeatMissing("source", "jenshaase.uimaScala.toolkit.types.DocumentAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_source, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public DocumentAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_source = jcas.getRequiredFeatureDE(casType, "source", "uima.cas.String", featOkTst);
    casFeatCode_source  = (null == casFeat_source) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_source).getCode();

  }
}



    