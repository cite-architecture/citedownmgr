package edu.holycross.shot.citedownmgr

import org.pegdown.Extensions
import org.pegdown.PegDownProcessor

import com.google.common.io.Files
import org.apache.commons.io.FilenameUtils


import edu.harvard.chs.citedown.ast.RootNode

import edu.harvard.chs.citedown.PegDownProcessor
import edu.harvard.chs.citedown.Extensions

import edu.harvard.chs.citedownutils.MarkdownUtil

import edu.harvard.chs.cite.CiteUrn


/**
* Class for use especially with gradle build, with main method 
* generating complete Leanpub-compatible source from citedown archive.
*/
class Leanpub {

  Leanpub() {
  }


  /** Q&D main method for invocation from build.gradle leanpub task.
   */
  public static void main(String [] arg) 
  throws Exception {
    File src = new File(arg[0])
    File outDir = new File(arg[1])
    String imgSvc = arg[2]
    ArrayList imgCollections = ["${arg[3]}"]
    String cts = arg[4]
    String cc = arg[5]


    SiteBuilder sb = new SiteBuilder(src)    
    sb.configureImages(imgSvc, imgCollections)
    sb.cts = cts
    sb.debug = 2

    System.err.println "Image collections: " + imgCollections

    sb.leanpub(outDir)
  }





}

