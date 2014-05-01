package edu.holycross.shot.citedownutils

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestTreeWalker extends GroovyTestCase {



  File testset1 = new File("testdata/testset1")
  File testset2 = new File("testdata/testset2")
  File testset3 = new File("testdata/testset3")


  void testFlatAlpha() {
    SiteBuilder sb = new SiteBuilder(testset1)
    ArrayList fileList = sb.sequenceFiles(testset1, [])
    assert fileList.size() == 3
    ArrayList expectedNames = ["file1.md", "file2.md", "tail.md"]
    fileList.eachWithIndex { f, i ->
      assert f.getName() == expectedNames[i]
    }
  }


  void testTwoTierAlpha() {
    SiteBuilder sb = new SiteBuilder(testset2)
    ArrayList fileList = sb.sequenceFiles(testset2, [])
    ArrayList expectedNames = ["file1.md", "file2.md", "tail.md", "sub1file.md", "sub2file.md"]
    fileList.eachWithIndex { f, i ->
      assert f.getName() == expectedNames[i]
    }
  }

  void testFlatToc() {
    SiteBuilder sb = new SiteBuilder(testset3)
    ArrayList fileList = sb.sequenceFiles(testset3, [])
    ArrayList expectedNames =  ["zfirst.md", "ymiddle.md", "xlast.md"]
    fileList.eachWithIndex { f, i ->
      assert f.getName() == expectedNames[i]
    }
  }


 
}
