package edu.holycross.shot.citedownmgr

import static org.junit.Assert.*
import org.junit.Test

import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import edu.harvard.chs.citedownutils.*

import edu.harvard.chs.cite.CiteUrn

/**
*/
class TestCdConvert extends GroovyTestCase {


  
  File testset7 = new File("testdata/testset7")
  File testFile = new File(testset7, "intro.md")
  File testFile2 = new File(testset7, "filteredintro.md")

  String imgsvc =  "http://pelike.hpcc.uh.edu/hmtdigital/images"
  String cts =  "http://pelike.hpcc.uh.edu/hmtdigital/texts"
  ArrayList imgColls = ["urn:cite:hmt:vaimg"]

  void testMixedReferences() {
    File leanpubDir = new File("testdata/testoutput")
    if (! leanpubDir.exists()) {
      leanpubDir.mkdir()
    }
    SiteBuilder sb = new SiteBuilder(testset7)    
    sb.cts = cts
    sb.imgSvc = imgsvc
    sb.imgCollections = imgColls

    String md = sb.convertToMarkdown(testFile)

    System.err.println "CONVERTED: \n\n" + md
    
    File converted = new File(leanpubDir, "testout.md") 
    converted.setText(md, "UTF-8")
    
    md = sb.convertToMarkdown(testFile2)
    System.err.println "\n\nSECOND CONVERSIONL: \n\n" + md

    File converted2 = new File(leanpubDir, "testout2.md") 
    converted2.setText(md, "UTF-8")
    

    
    
  }


 
}
