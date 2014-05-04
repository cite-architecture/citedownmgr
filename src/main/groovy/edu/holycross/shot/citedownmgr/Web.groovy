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
* generating complete bfdocs-compatible source from citedown archive.
*/
class Web {

  String cdRoot
  File targetDir

  Web(String pathRoot, File outputDir) {
    this.cdRoot = pathRoot
    this.targetDir = outputDir
  }




  String htmlForCd(File f) 
  throws Exception{
    try {
      String path1 = f.toString()
      String relativeFile = path1.replaceFirst(cdRoot, '')

      // Use apache filenameutils to check for:
      // no extension
      // .md
      // .markdown

      return "${targetDir}/${relativeFile}".replaceFirst(/.md$/,".html")
    } catch (Exception e ) {
      throw e
    }
  }


  String formatPage (String articleText, String pageTitle, WebConfig conf, String up, String prev, String next) {
    return "Prev ${prev} | Next ${next}"
  }


  /** Q&D main method for invocation from build.gradle leanpub task.
   */
  public static void main(String [] arg) 
  throws Exception {
  }





}




