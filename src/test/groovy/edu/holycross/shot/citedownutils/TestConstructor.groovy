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
    cantRead.setReadable(false,false)
    assert shouldFail {
      SiteBuilder illegible = new SiteBuilder(cantRead)
    }
    cantRead.delete()
    
    SiteBuilder sb = new SiteBuilder(testset1)
    assert sb
  }

 
}
