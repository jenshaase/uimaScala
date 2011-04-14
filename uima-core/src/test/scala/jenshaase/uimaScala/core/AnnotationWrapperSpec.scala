/*
* Copyright (C) 2011 by Jens Haase <je.haase@googlemail.com>
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

package jenshaase.uimaScala.core

import org.specs2.mutable.Specification
import Implicits._
import org.apache.uima.util.CasCreationUtils
import org.uimafit.factory.{TypePrioritiesFactory, TypeSystemDescriptionFactory}
import org.apache.uima.jcas.JCas
import org.apache.uima.jcas.tcas.Annotation
import util.Helper

/**
 * @author Jens Haase <je.haase@googlemail.com>
 */
class AnnotationWrapperSpec extends Specification with Helper {
  
  "Annotation Wrapper" should {
    
    "trim a annotation" in {
      val cas = newJCas
      cas.setDocumentText("This is text")
      
      val a = new Annotation(cas, 4, 8)
      a.getCoveredText must be equalTo(" is ")
      a.trim.getCoveredText must be equalTo("is")
    }
    
    "check if a annotation is empty" in {
      new Annotation(newJCas, 0, 0).isEmpty must beTrue
      new Annotation(newJCas, 0, 1).isEmpty must beFalse
    }
  }
}