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
class TestLeanpub extends GroovyTestCase {


  File testset1 = new File("testdata/testset1")
  File testset7 = new File("testdata/testset7")
  File testset5 = new File("testdata/testset5")

  String imgsvc =  "http://pelike.hpcc.uh.edu/hmtdigital/images"
  String cts =  "http://pelike.hpcc.uh.edu/hmtdigital/texts"
  ArrayList imgColls = ["urn:cite:hmt:vaimg"]

  void testMixedReferences() {
    File leanpubDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset7)    
    sb.configureImages(imgsvc, imgColls)
    sb.cts = cts

    System.err.println "Configured site builder. Now invoke leanpub()"
    sb.leanpub(leanpubDir)

    // add tests: on num files in dir, num image files in image dir?

    //leanpubDir.deleteDir()

  }


 
}
