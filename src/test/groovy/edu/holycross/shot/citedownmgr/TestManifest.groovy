package edu.holycross.shot.citedownmgr

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestManifest extends GroovyTestCase {



  File testset1 = new File("testdata/testset1")


  void testManifest() {
    SiteBuilder sb = new SiteBuilder(testset1)
    assert sb
    //def jsonString = sb.writeBfDocsManifest()
    // parse json and verify structure...
  }

 
}
