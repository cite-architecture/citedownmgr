package edu.holycross.shot.citedownmgr

import static org.junit.Assert.*
import org.junit.Test

import org.apache.commons.io.FilenameUtils

/**
*/
class TestWebFiles extends GroovyTestCase {



  File testset6 = new File("testdata/testset6/")
  File webDir = new File("testdata/web/")

  /* test constructor requirements */
  void testConstructor() {
    SiteBuilder sb = new SiteBuilder(testset6)
    String root = FilenameUtils.getPath("${sb.fileSequence[0]}")
    Web web = new Web(root, webDir)
    assert web.cdRoot == "testdata/testset6/"


    sb.fileSequence.each { f ->
      System.err.println web.htmlForCd(f)
    }

    sb.web(webDir)



  }

 
}
