package edu.holycross.shot.citedownutils

import org.pegdown.Extensions
import org.pegdown.PegDownProcessor


/**
* Works with one or more citedown source files in a directory hierarchy
* to create multiple kinds of output.
*/
class SiteBuilder {

  /** Root directory of source in citedown format. */
  File mdRoot

  /** Ordered list of files to process. */
  java.util.ArrayList fileOrder = []

  /** Character encoding for citedown source.  Default is UTF-8. */
  String inputEncoding = "UTF-8"

  /** Character encoding for output.  Default is UTF-8. */
  String outputEncoding = "UTF-8"



  /** FilenameFilter defining files to process or ignore. 
   *  The goal is process all .md files, and all directories.
   *  Rather than try to define a pattern that would match 
   *  all possible directory names, we accept any name except
   *  for the negative definitions here: ignore the magic files 
   *  toc.txt and web.properties, invisible (dot) files, and all files 
   * ending in extensions for the image types we use (jpeg, png).
   */
  FilenameFilter exclusionFilter = [accept: {d, f -> f.toLowerCase() != 'toc.txt' && f.toLowerCase() != 'web.properties' && ! (f.toLowerCase() ==~ /.+~/) && !(f.toLowerCase() ==~ /.+.jpg/) &&!(f.toLowerCase() ==~ /.+.png/) && !(f.toLowerCase() ==~ /.+.jpeg/) && (f[0] != '.')}] as FilenameFilter


  /** Verifies status of source and output directories,
   * and creates the latter if necessary.
   * @param srcDir Root directory of markdown source.
   * @param outputDir Root directory for writing HTML output.
   * @throws Exception if file permissions on either source or
   * output directory don't work.
   */

  /*
  void setUpDirectories(File srcDir, File outputDir) 
  throws Exception {
    if (!outputDir.exists()) {
      outputDir.mkdir()
    }
    if (!outputDir.canWrite()) {
      throw new Exception("Cannot write to output directory ${outputDir}")
    }
    //this.htmlRoot = outputDir

    if (!srcDir.canRead()) {
      throw new Exception("Cannot read source directory ${srcDir}")
    }
    this.mdRoot = srcDir
  }

  */
  
  /** Constructor defining root directory for citedown source.
   * @param srcDir Root directory of markdown source.
   * @throws Exception if srcDir not an extant readable directory.
   */
  SiteBuilder(File srcDir) {
    if (!srcDir.canRead()) {
      throw new Exception("Cannot read source directory ${srcDir}")
    }
    this.mdRoot = srcDir
    this.fileOrder = sequenceFiles(this.mdRoot, [])
  }



     /** Constructor defining directories for markdown
     * source and HTML output.
     * @param srcDir Root directory of markdown source.
     * @param outputDir Root directory for writing HTML output.
     * @throws Exception if file permissions on either source or
     * output directory don't work.
     */

  /*
     SiteBuilder(File srcDir, File outputDir) 
     throws Exception {
         try {
             setUpDirectories(srcDir,outputDir)
         } catch (Exception e) {
             throw e
         }
     }
  */

     /** Main method for stand-alone execution.
     * @param arg Main methods expects two arguments naming paths
     * to source directory and output directory respectively.
     * @throws Exception if wrong number of arguments, or if 
     * directory permissions are not correct.
     */

  /*
     public static void main(String [] arg) 
     throws Exception {
         if (arg.size() != 2) {
             throw new Exception("SiteBuilder usage: java edu.holycross.shot.mdweb.SiteBuilder <SOURCEDIRECTORY> <OUTPUTDIRECTORY>")
         }
         try {
             File src = new File(arg[0])
             File out = new File(arg[1])
             SiteBuilder sb = new SiteBuilder(src,out)
             sb.buildSite()
         } catch (Exception e) {
             throw e
         }
     }

  */


     /** Recursively builds web site from a directory that has
     * a table of contents file.
     * @param dir Root of directory tree from which to begin
     * or continue recursive processing of markdown source.
     * @param conf The initial or inherited web configuration settings.
     * @param upLink String giving a link to a parent directory. May be
     * empty if dir is the root of the entire tree to process.
     * @param toc The table of contents file.
     */

  //// NOT YET ////
  /*
     void buildByToc(File dir, WebConfig conf, String upLink, File toc) {
         //System.err.println "BUILD BY TOC: ${toc}"
         def tocList = []
         def titleList = []
         if (toc.exists()) {
             toc.eachLine { l ->
                 if ((l.size() > 0) && (l[0] != '#')) {
                     def parts = l.split(/=/)
                     if (parts.size() == 2) {
                         tocList.add(parts[0])
                         titleList.add(parts[1])

                     } else {
                         tocList.add(l)
                         titleList.add("mdweb")
                     }
                 } 
             }
         }

         tocList.eachWithIndex { fName, i ->
             def prevnext = getPrevNext(dir, fName, tocList, i)
             File f = new File("${dir}/${fName}")              

             String pgTitle = titleList[i]
             if (fName ==~ /.+md/) {
                 if (pgTitle == "mdweb") {
                     def hdr = new StringWriter()
                     f.filterLine(hdr) { it =~ /^[ ]*#/ }
                     pgTitle = hdr.toString().replace(/#/, '')
                 }
                 String htmlText = this.makePage(f.getText(this.inputEncoding))
                 File htmlFile = new File(htmlForMd(f))
                 htmlFile.write(formatPage(htmlText, pgTitle, conf, upLink, prevnext['prev'], prevnext['next']), this.outputEncoding)
            }
        }

        upLink = "../" + firstPageForDirectory(dir)
        dir.eachDir { buildDirectory(it, conf, upLink) }
    }
*/
  
  /** Recursively builds web site from a directory that lacks
   * a table of contents file.
   * @param dir Root of directory tree from which to begin
   * or continue recursive processing of markdown source.
   * @param conf The initial or inherited web configuration settings.
   * @param upLink String giving a link to a parent directory. May be
   * empty if dir is the root of the entire tree to process.
   */




  /** Recursively walks directory tree and collects filenames in
   * proper order.
   * Checks for presence of toc file, and, if present, uses its sequence.
   * Otherwise, follows alphabetical order.
   * @param dir Root directory to begin from.
   * @param fileList List of previously collected files to append to.
   * @returns Ordered list of file names.
   */
  java.util.ArrayList sequenceFiles(File dir, ArrayList fileList) {
    File toc = new File("${dir}/toc.txt") 
    if (toc.exists()) {
      return getFileNamesByToc(dir,fileList)
      //buildByToc(dir,conf, upLink, toc)
    } else {
      return getFileNamesAlphabetically(dir,fileList)
    }
  }



  // Exceptoin if toc file does not exist; Exception if file lised in toc.txt
  // does not exist.
  java.util.ArrayList getFileNamesByToc(File dir, ArrayList files) {
    File toc  = new File(dir, "toc.txt")

    if (!toc.exists()) {
      throw new Exception("SiteBuilder: could not find requesed table of contens file ${dir}/toc.txt")
    }

    toc.eachLine { tocEntry ->
      File entryFile
      // blank lines allowed; comments with '#' ignored:
      if ((tocEntry.size() > 0) && (tocEntry[0] != '#')) {
	// check for either of two legal forms of listing:  
	// 1: a bare file name, or 2: filename=title

	ArrayList parts = tocEntry.split(/=/)
	if (parts.size() == 2) {
	  entryFile = new File(dir,parts[0])
	} else {
	  entryFile = new File(dir,tocEntry)
	}
      }
      // file must exist:
      if (! entryFile.exists()) {
	throw new Exception("SiteBuilder: file ${entryFile} listed in ${dir}/toc.txt but file does not exist.")
      } 
      // recurse into subdirectories, add plain files to list:
      if (entryFile.isDirectory()) {
	files = sequenceFiles(entryFile,files) 
      } else {
	files.add(entryFile)
      }
    }
    return files
  }



  /** For a directory lacking a toc.txt file, collects all file names
   * alphabetically, and recursively sequences subdirectories.
   * @param dir Directory to analyze.
   * @param files Ordered list of previously collected files.
   * @returns An ordered List of File objects.
   */
  java.util.ArrayList getFileNamesAlphabetically(File dir, ArrayList files) {
    ArrayList fileList = dir.list(exclusionFilter).toList()
    fileList.eachWithIndex { fName, i -> 
      File f = new File("${dir}/${fName}")                
      if (! f.isDirectory()) {
	files.add(f)
      }
    }
    dir.eachDir { subdir ->
      files = sequenceFiles(subdir,files) 
    }
    return files
  }
}

