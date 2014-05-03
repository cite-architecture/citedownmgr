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
class TestCdToMd extends GroovyTestCase {


  File testset1 = new File("testdata/testset1")
  File testset2 = new File("testdata/testset2")
  File testset5 = new File("testdata/testset5")

  String imgsvc =  "http://pelike.hpcc.uh.edu/hmtdigital/images"
  String cts =  "http://pelike.hpcc.uh.edu/hmtdigital/texts"



  void testFlatCorpusRewrite() {
    File outputDir = new File ("testdata/testoutput")
    if (! outputDir.exists()) {
      outputDir.mkdir()
    }
    File flatCopyDir = new File("testdata/testoutput/flattened")
    File filteredDir = new File("testdata/testoutput/filtered")
    File pureMd = new File("testdata/testoutput/markdown")

    SiteBuilder sb = new SiteBuilder(testset1)    
    sb.configureImages(imgsvc, ["urn:cite:hmt:vaimg"])
    sb.cts = cts
    

    ArrayList srcList = sb.flatCopy(flatCopyDir)
    ArrayList modifiedList = sb.rewriteImageReff(srcList, filteredDir)
    assert srcList.size() == modifiedList.size()


    // THIS IS BAD:
    sb.cdToMd(modifiedList, pureMd)

    // flatCopyDir.deleteDir()
  }


 
}
