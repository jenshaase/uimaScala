package org.apache.uima.tools.jcasgen

import org.apache.uima.resource.metadata.TypeDescription
import scala.collection.JavaConversions._

class UimaScala_TypeTemplate extends Jg.IJCasTypeTemplate {

  def generate(argument: Any): String = {
    val args: Array[Any] = argument.asInstanceOf[Array[Any]]
    val jg = args(0).asInstanceOf[Jg]
    val td = args(1).asInstanceOf[TypeDescription]
    val stringBuffer = new StringBuffer()

    jg.packageName = jg.getJavaPkg(td);
    if (0 != jg.packageName.length()) {
      stringBuffer.append("package ");
      stringBuffer.append(jg.packageName);
      stringBuffer.append(";\n");
    }
    stringBuffer.append("""
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
""");

    if (td.getFeatures().length > 0) {
      stringBuffer.append("""import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
""");
    }

    stringBuffer.append("");

    jg.collectImports(td, true).foreach { imp =>
      if (!imp.equals(jg.getJavaNameWithPkg(td.getName()+"_Type"))) {
        stringBuffer.append(s"""import ${imp};""")
        stringBuffer.append("\n")
      }
    }

    stringBuffer.append("\n");
    val typeName = jg.getJavaName(td);
    val typeName_Type = typeName + "_Type";
    stringBuffer.append(s"""/** ${jg.nullBlank(td.getDescription())} */
public class ${typeName_Type} extends ${jg.getJavaName(td.getSupertypeName())}_Type {
  /**
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}

  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (${typeName_Type}.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ${typeName_Type}.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ${typeName}(addr, ${typeName_Type}.this);
  			   ${typeName_Type}.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ${typeName}(addr, ${typeName_Type}.this);
  	  }
    };

  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ${typeName}.typeIndexID;

  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("${td.getName()}");
""");
    

    td.getFeatures().foreach { fd =>
      val featName = fd.getName();
      val featUName = jg.uc1(featName);  // upper case first letter

      val rangeType = jg.getJavaRangeType(fd);
      val getSetNamePart = jg.sc(rangeType);
      val returnType = if (getSetNamePart.equals("Ref")) "int" else rangeType;
      val getSetArrayNamePart = jg.getGetSetArrayNamePart(fd);
      
      val elemType =
        if (jg.sc(jg.getJavaRangeArrayElementType(fd)).equals("Ref")) {
          "int";
        } else {
          jg.getJavaRangeArrayElementType(fd);
        }
      val casFeatCode = "casFeatCode_" + featName;

      stringBuffer.append(s""" 
  final Feature casFeat_${featName};
  final int     ${casFeatCode};
  /**
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public ${returnType} get${featUName}(int addr) {
        if (featOkTst && casFeat_${featName} == null)
      jcas.throwFeatMissing("${featName}", "${td.getName()}");
    return ll_cas.ll_get${getSetNamePart}Value(addr, ${casFeatCode});
  }
  /**
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void set${featUName}(int addr, ${returnType} v) {
        if (featOkTst && casFeat_${featName} == null)
      jcas.throwFeatMissing("${featName}", "${td.getName()}");
    ll_cas.ll_set${getSetNamePart}Value(addr, ${casFeatCode}, v);}
    
 """);
      
      if (jg.hasArrayRange(fd)) {
        stringBuffer.append(s"""
  /**
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public ${elemType} get${featUName}(int addr, int i) {
        if (featOkTst && casFeat_${featName} == null)
      jcas.throwFeatMissing("${featName}", "${td.getName()}");
    if (lowLevelTypeChecks)
      return ll_cas.ll_get${getSetArrayNamePart}ArrayValue(ll_cas.ll_getRefValue(addr, ${casFeatCode}), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, ${casFeatCode}), i);
	return ll_cas.ll_get${getSetArrayNamePart}ArrayValue(ll_cas.ll_getRefValue(addr, ${casFeatCode}), i);
  }
   
  /**
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void set${featUName}(int addr, int i, ${elemType} v) {
        if (featOkTst && casFeat_${featName} == null)
      jcas.throwFeatMissing("${featName}", "${td.getName}");
    if (lowLevelTypeChecks)
      ll_cas.ll_set${getSetArrayNamePart}ArrayValue(ll_cas.ll_getRefValue(addr, ${casFeatCode}), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, ${casFeatCode}), i);
    ll_cas.ll_set${getSetArrayNamePart}ArrayValue(ll_cas.ll_getRefValue(addr, ${casFeatCode}), i, v);
  }
""");        
      }
      stringBuffer.append(" \n");
    }

    stringBuffer.append("\n");

    if (td.getName().equals("uima.cas.Annotation")) {
      stringBuffer.append("  ");
      stringBuffer.append(s"""  /** @see org.apache.uima.cas.text.AnnotationFS#getCoveredText() 
    * @param inst the low level Feature Structure reference 
    * @return the covered text 
    */ 
  public String getCoveredText(int inst) { 
    final CASImpl casView = ll_cas.ll_getSofaCasView(inst);
    final String text = casView.getDocumentText();
    if (text == null) {
      return null;
    }
    return text.substring(getBegin(inst), getEnd(inst)); 
  }
""");
    } /* of Annotation if-statement */

    stringBuffer.append(s"""

  /** initialize variables to correspond with Cas Type and Features
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ${typeName_Type}(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

""");
    td.getFeatures().foreach { fd =>
      val featName = fd.getName();

      stringBuffer.append(s""" 
    casFeat_${featName} = jcas.getRequiredFeatureDE(casType, "${featName}", "${fd.getRangeTypeName()}", featOkTst);
    casFeatCode_${featName}  = (null == casFeat_${featName}) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_${featName}).getCode();

""");
    }
    stringBuffer.append("  }\n}\n\n\n\n    ");
    return stringBuffer.toString();
  }
}
