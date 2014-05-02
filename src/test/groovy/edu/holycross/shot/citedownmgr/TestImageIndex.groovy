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
class TestImageIndex extends GroovyTestCase {





  File testset1 = new File("testdata/testset1")
  File testset2 = new File("testdata/testset2")
  File testset5 = new File("testdata/testset5")

  String svc =  "http://pelike.hpcc.uh.edu/hmtdigital/images"

  void testImageConf() {
    SiteBuilder sb = new SiteBuilder(testset5)    
    assert sb.imagesConfigured() == false
    sb.configureImages(svc, ["urn:cite:hmt:vaimg"])
    assert sb.imagesConfigured()
  }

  void testFlatCorpusRetrieval() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset1)    
    sb.configureImages(svc, ["urn:cite:hmt:vaimg"])
    sb.retrieveImages(outputDir)
    // test count of images in outputDir == 3
    assert outputDir.list().size() == 3
    outputDir.deleteDir()
  }


  void testTwoTierRetrieval() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset2)    
    sb.configureImages(svc, ["urn:cite:hmt:vaimg"])
    sb.retrieveImages(outputDir)
    // test count of images in outputDir == 3
    assert outputDir.list().size() == 3
    outputDir.deleteDir()
  }



  void testComplexArchive() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset5)    
    sb.configureImages(svc, ["urn:cite:hmt:vaimg"])
    sb.retrieveImages(outputDir)
    // test count of images in outputDir == 3
    assert outputDir.list().size() == 3
    outputDir.deleteDir()
  }

 
}
