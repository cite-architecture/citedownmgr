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
class TestFlatMdMain extends GroovyTestCase {


  File testset1 = new File("testdata/testset1")
  File testset7 = new File("testdata/testset7")
  File testset5 = new File("testdata/testset5")

  String imgsvc =  "http://pelike.hpcc.uh.edu/hmtdigital/images"
  String cts =  "http://pelike.hpcc.uh.edu/hmtdigital/texts"
  ArrayList imgColls = ["urn:cite:hmt:vaimg"]

  void testMixedReferences() {
    


    String citedownDir =    "testdata/testset7"
    String outputDir = "testdata/testoutputmain"
    
    String imgSvc = "http://pelike.hpcc.uh.edu/hmtdigital/images"
    String  cts = "http://pelike.hpcc.uh.edu/hmtdigital/texts"
    String  cc = "http://pelike.hpcc.uh.edu/hmtdigital/collections"

    String  imgCollection = "urn:cite:hmt:vaimg"


    String[] argStrings = [citedownDir, outputDir, imgSvc, imgCollection, cts, cc, "Test archive"]
    System.err.println "Here are args for main:"
    System.err.println argStrings
    System.err.println "Now running main:"
    FlatMarkdown.main(argStrings)


  }


 
}
