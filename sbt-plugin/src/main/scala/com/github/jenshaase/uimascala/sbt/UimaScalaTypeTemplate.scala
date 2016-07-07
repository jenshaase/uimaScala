package org.apache.uima.tools.jcasgen

import org.apache.uima.resource.metadata.TypeDescription
import scala.collection.JavaConversions._

class UimaScalaTypeTemplate extends Jg.IJCasTypeTemplate {

  def generate(argument: Any): String = {
    val stringBuffer = new StringBuffer();
    stringBuffer.append("\n\n");

    val args: Array[Object] = argument.asInstanceOf[Array[Object]]
    val jg = args(0).asInstanceOf[Jg]
    val td = args(1).asInstanceOf[TypeDescription]
    jg.packageName = jg.getJavaPkg(td);

    if (0 != jg.packageName.length()) {
      stringBuffer.append(s"""package ${jg.packageName};""");
      stringBuffer.append("\n");
    }
    else
      jg.error.newError(IError.WARN,
		    jg.getString("pkgMissing", Array.apply[Object](td.getName)), null);
    stringBuffer.append("""
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

""");

    jg.collectImports(td, false).foreach { imp =>
      stringBuffer.append(s"""import $imp;""");
      stringBuffer.append("\n");
    }

    stringBuffer.append("\n\n");

    val typeName = jg.getJavaName(td);
    val typeName_Type = typeName + "_Type";
    val jcasTypeCasted = "((" + typeName_Type + ")jcasType)";

    stringBuffer.append(s"""/** ${jg.nullBlank(td.getDescription())} */
public class ${typeName} extends ${jg.getJavaName(td.getSupertypeName())} {
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(${typeName}.class);
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor */
  protected ${typeName}() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   *
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ${typeName}(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /**
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ${typeName}(JCas jcas) {
    super(jcas);
    readObject();   
  } 
""");

    if (jg.isSubTypeOfAnnotation(td)) {
      stringBuffer.append(s"""
  /**
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ${typeName}(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   
""");
    }

    stringBuffer.append(s"""
  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   */
  private void readObject() {/*default - does nothing empty block */}
     
""");

    td.getFeatures().foreach { fd =>
      val featName = fd.getName();
      val featUName = jg.uc1(featName);  // upper case first letter
	    if (Jg.reservedFeatureNames.contains(featUName))
	      jg.error.newError(IError.ERROR,
		      jg.getString("reservedNameUsed", Array.apply[Object](featName, td.getName)),
		      null);

      val featDesc = jg.nullBlank(fd.getDescription());
      val featDescCmt = featDesc;

      val rangeType = jg.getJavaRangeType(fd);
      val elemType = jg.getJavaRangeArrayElementType(fd);

      stringBuffer.append(s""" 
    
  //*--------------*
  //* Feature: ${featName}

  /** getter for ${featName} - gets ${featDescCmt}
   * @return value of the feature 
   */
  public ${rangeType} get${featUName}() {
    if (${typeName_Type}.featOkTst && ${jcasTypeCasted}.casFeat_${featName} == null)
      jcasType.jcas.throwFeatMissing("${featName}", "${td.getName}");
    return ${jg.getFeatureValue(fd, td)};}
    
  /** setter for ${featName} - sets ${featDescCmt} 
   * @param v value to set into the feature 
   */
  public void set${featUName}(${rangeType} v) {
    if (${typeName_Type}.featOkTst && ${jcasTypeCasted}.casFeat_${featName} == null)
      jcasType.jcas.throwFeatMissing("${featName}", "${td.getName()}");
    ${jg.setFeatureValue(fd, td)};}    
  """);

      if (jg.hasArrayRange(fd)) {
        stringBuffer.append(s"""  
  /** indexed getter for ${featName} - gets an indexed value - ${featDescCmt}
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public ${elemType} get${featUName}(int i) {
    if (${typeName_Type}.featOkTst && ${jcasTypeCasted}.casFeat_${featName} == null)
      jcasType.jcas.throwFeatMissing("${featName}", "${td.getName()}");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ${jcasTypeCasted}.casFeatCode_${featName}), i);
    return ${jg.getArrayFeatureValue(fd, td)};}

  /** indexed setter for ${featName} - sets an indexed value - ${featDescCmt}
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void set${featUName}(int i, ${elemType} v) { 
    if (${typeName_Type}.featOkTst && ${jcasTypeCasted}.casFeat_${featName} == null)
      jcasType.jcas.throwFeatMissing("${featName}", "${td.getName()}");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ${jcasTypeCasted}.casFeatCode_${featName}), i);
    ${jg.setArrayFeatureValue(fd, td)};}
  """);
      } /* of hasArray */

      stringBuffer.append("");

    } /* of Features iteration */

    stringBuffer.append("");

    if (td.getName().equals("uima.cas.Annotation")) {
      stringBuffer.append("  ");
      stringBuffer.append("""  /** Constructor with begin and end passed as arguments 
    * @param jcas JCas this Annotation is in
    * @param begin the begin offset
    * @param end the end offset
    */
  public Annotation(JCas jcas, int begin, int end) { 
	  this(jcas); // forward to constructor 
	  this.setBegin(begin); 
	  this.setEnd(end); 
  } 
  
  /** @see org.apache.uima.cas.text.AnnotationFS#getCoveredText() 
    * @return the covered Text 
    */ 
  public String getCoveredText() { 
    final CAS casView = this.getView();
    final String text = casView.getDocumentText();
    if (text == null) {
      return null;
    }
    return text.substring(getBegin(), getEnd());
  } 
  
  /** @deprecated 
    * @return the begin offset 
    */
  public int getStart() {return getBegin();}
""");
      stringBuffer.append("");
    } /* of Annotation if-statement */
    stringBuffer.append("}\n\n    ");
    return stringBuffer.toString();
  }
}
