package edu.holycross.shot.citedownmgr

import static org.junit.Assert.*
import org.junit.Test


/**
*/
class TestFlatCopy extends GroovyTestCase {


  File testset1 = new File("testdata/testset1")
  File testset5 = new File("testdata/testset5")
  File testset6 = new File("testdata/testset6")

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


  void testBadInputList() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset1)
    ArrayList bogusList = ["notafile.txt","another-completely-fake-file.md"]
    shouldFail {
      ArrayList copyList = sb.flatCopy(bogusList, outputDir)
    }
  }

  void testFunnyNames() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset6)
    ArrayList outputList =  sb.flatCopy(outputDir)
    outputList.eachWithIndex { f, i ->
      assert org.apache.commons.io.FileUtils.contentEquals(f,sb.fileSequence[i])
    }
    outputDir.deleteDir()
  }
 

  void testSimpleFlat() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset1)
    ArrayList outputList = sb.flatCopy(outputDir)
    outputList.eachWithIndex { f, i ->
      assert org.apache.commons.io.FileUtils.contentEquals(f,sb.fileSequence[i])
    }
    outputDir.deleteDir()
  }

  void testMultiTier() {
    File outputDir = new File("testdata/testoutput")
    SiteBuilder sb = new SiteBuilder(testset5)
    ArrayList outputList =  sb.flatCopy(outputDir)
    outputList.eachWithIndex { f, i ->
      assert org.apache.commons.io.FileUtils.contentEquals(f,sb.fileSequence[i])
    }


    ArrayList expectedFiles = ["zfirst.md","ymiddle.md","sub1.md","index.md","index-1.md","xlast.md"]
    Integer lineCount = 0
    File books = new File("testdata/testoutput/Books.txt")
    books.eachLine { ln ->
      assert ln == expectedFiles [lineCount]
      lineCount++
    }

    outputDir.deleteDir()
  }



}
