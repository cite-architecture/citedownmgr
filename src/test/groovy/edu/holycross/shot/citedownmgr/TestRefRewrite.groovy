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
class TestRefRewrite extends GroovyTestCase {


  File testset1 = new File("testdata/testset1")
  File testset2 = new File("testdata/testset2")
  File testset5 = new File("testdata/testset5")

  String svc =  "http://pelike.hpcc.uh.edu/hmtdigital/images"

  void testFlatCorpusRewrite() {

    File flatCopyDir = new File("testdata/testoutput")
    File filteredDir = new File("testdata/testoutput/filtered")
    SiteBuilder sb = new SiteBuilder(testset1)    
    sb.configureImages(svc, ["urn:cite:hmt:vaimg"])

    ArrayList srcList = sb.flatCopy(flatCopyDir)
    sb.rewriteImageReff(srcList, filteredDir)
    //outputDir.deleteDir()
  }


 
}
