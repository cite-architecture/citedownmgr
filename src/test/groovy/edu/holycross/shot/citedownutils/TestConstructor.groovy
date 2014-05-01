package edu.holycross.shot.citedownutils

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestConstructor extends GroovyTestCase {



  File testset1 = new File("testdata/testset1")

  /* test constructor requirements */
  void testConstructor() {
    File bogusFile = new File("NON-EXISTANT-DIRECTORY")
    assert shouldFail {
      SiteBuilder bogus = new SiteBuilder(bogusFile)
    }

    File cantRead = new File("testdata/writeonly")
    cantRead.mkdir()
    cantRead.setReadable(false,false)
    assert shouldFail {
      SiteBuilder illegible = new SiteBuilder(cantRead)
    }
    cantRead.deleteDir()
    
    SiteBuilder sb = new SiteBuilder(testset1)
    assert sb
  }

 
}
