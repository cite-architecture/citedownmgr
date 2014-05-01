package edu.holycross.shot.citedownutils

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestTreeWalker extends GroovyTestCase {



  File testset1 = new File("testdata/testset1")
  File testset2 = new File("testdata/testset2")

  void testFlat() {
    SiteBuilder sb = new SiteBuilder(testset1)
    ArrayList fileList = sb.sequenceFiles(testset1, [])
    assert fileList.size() == 3
    ArrayList expectedNames = ["file1.md", "file2.md", "tail.md"]
    fileList.eachWithIndex { f, i ->
      assert f.getName() == expectedNames[i]
    }
  }


  void testTwoTiers() {
    SiteBuilder sb = new SiteBuilder(testset2)
    ArrayList fileList = sb.sequenceFiles(testset2, [])
    ArrayList expectedNames = ["file1.md", "file2.md", "tail.md", "sub1file.md", "sub2file.md"]
    fileList.eachWithIndex { f, i ->
      assert f.getName() == expectedNames[i]
    }

  }
 
}
