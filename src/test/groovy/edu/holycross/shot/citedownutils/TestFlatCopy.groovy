package edu.holycross.shot.citedownutils

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestFlatCopy extends GroovyTestCase {


  File testset1 = new File("testdata/testset1")


  void testDirectoryPerms() {
    File newOutputDir = new File("testdata/NON-EXISTANT-DIRECTORY")
    SiteBuilder sb = new SiteBuilder(testset1)
    sb.flatCopy(newOutputDir)
    newOutputDir.deleteDir()
    
    File cantWrite = new File("testdata/readonly")
    cantWrite.mkdir()
    cantWrite.setReadOnly()
    shouldFail {
      sb.flatCopy(cantWrite)
    }
    cantWrite.delete()
  }

  void testSimpleFlat() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset1)
    sb.flatCopy(outputDir)

    ArrayList outputList = sb.sequenceFiles(outputDir)
    outputList.eachWithIndex { f, i ->
      assert f.length() == sb.fileSequence[i].length()
    }
    outputDir.deleteDir()
  }


 
}
