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





  void testBinaryFileDownload() {
    File f = new File("testdata/testset1/file2.md")
    ImageRetriever imgRetriever = new ImageRetriever(f)


    String imgUrl = "http://pelike.hpcc.uh.edu/hmtdigital/images?request=GetBinaryImage&urn=urn:cite:hmt:vaimg.VA001VN-0503@0.176,0.2855,0.137,0.0834"
    Integer expectedSize = 6408

    File outDir = new File("testdata/testoutput")
    outDir.mkdir()
    File img1 = new File(outDir,"img1.jpg")
    imgRetriever.download(imgUrl, img1)
    assert img1.size() == expectedSize
    outDir.deleteDir()
  }



  void testImageDownload() {
    // Construct and fully configure ImageRetriever:
    File f = new File("testdata/testset1/file2.md")
    ImageRetriever imgRetriever = new ImageRetriever(f)
    imgRetriever.configureImageCollections(["urn:cite:hmt:vaimg"])
    imgRetriever.mu.img = "http://pelike.hpcc.uh.edu/hmtdigital/images"

    // Retrieve images:
    File outDir = new File("testdata/testoutput")
    Integer imgCount = imgRetriever.retrieveImages(outDir)
    assert imgCount == 1
    outDir.deleteDir()
  }

 
}
