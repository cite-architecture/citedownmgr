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

import groovy.json.JsonBuilder

/**
* Works with one or more citedown source files in a directory hierarchy
* to create multiple kinds of output.
*/
class SiteBuilder {

  Integer debug = 0


  /** Root directory of source files in citedown format. */
  File mdRoot

  /** Ordered list of files to process. */
  java.util.ArrayList fileSequence = []

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
  FilenameFilter exclusionFilter = [accept: {d, f -> f.toLowerCase() != 'toc.txt' && f.toLowerCase() != 'web.properties' && ! (f.toLowerCase() ==~ /.+~/) && !(f.toLowerCase() ==~ /.+.jpg/) &&!(f.toLowerCase() ==~ /.+.png/) && !(f.toLowerCase() ==~ /.+.jpeg/) &&  !(f.toLowerCase() ==~ /.+.gif/) && (f[0] != '.')}] as FilenameFilter


  /** Base URL of CITE Image Service */
  String imgSvc

  /** List of any CITE Collections represented in imgSvc */
  ArrayList imgCollections

  /** Base URL of CTS */
  String cts

  /** Base URL of CITE Collection service */
  String cc

  /** Title for entire archive */
  String archiveTitle = "Untitled"


  /** Constructor defining root directory for citedown source.
   * @param srcDir Root directory of markdown source.
   * @throws Exception if srcDir not an extant readable directory.
   */
  SiteBuilder(File srcDir) 
  throws Exception {
    if (!srcDir.canRead()) {
      throw new Exception("Cannot read source directory ${srcDir}")
    }
    this.mdRoot = srcDir
    this.fileSequence = sequenceFiles(this.mdRoot)
  }


  /** Initializes settings needed to retrieve images */
  void  configureImages(String svcUrl, ArrayList collections) {
    this.imgSvc = svcUrl
    this.imgCollections = collections
  }


  /** Determines if object is configured to retrieve images.
   * @returns true if required settings are initialized.
   */
  boolean imagesConfigured() {
    if (this.imgSvc && this.imgCollections) {
      return true
    } else {
      return false
    }
  }


  /** Sequentially steps through each file in sequence and
   * downloads a local copy of any images quoted in the citedown.
   *
   * @param outDir  A writable directory for output.
   * @throws Exception if outDir is not writable, or if
   * SiteBuilder is not configured to retrieve images.
   */
  void retrieveImages(File outDir) 
  throws Exception {
    if (!outDir.exists()) {
      outDir.mkdir()
    }
    if (!outDir.canWrite()) {
      throw new Exception("SiteBuilder:retrieveImages: cannot write to output directory ${outDir}")
    }

    if (! imagesConfigured()) {
      throw new Exception("SiteBuilder:retrieveImages: not configured to retrieve images.")
    }

    if (debug > 0) {
      System.err.println "Retrieving images from ${this.imgSvc} from collections ${this.imgCollections} "
    }
    Integer imgCount = 1
    this.fileSequence.each { f ->
      if (debug > 0 ) {
	System.err.println "\tRetreive images from ${f}"
      }
      ImageRetriever imgRetriever = new ImageRetriever(f)
      imgRetriever.configureImageCollections(this.imgCollections)
      imgRetriever.mu.img =  this.imgSvc
      imgRetriever.mu.collectReferences()
      Integer retrieved = imgRetriever.retrieveImages(outDir, imgCount)
      imgCount += retrieved
    }
  }


  /** Uses an ImageRetrieve to get all reference definitions 
   * for images in a citedown source file
   * and returns them in a sorted list.
   * @param f File with contents in citedown.
   * @returns A sorted list of reference identifiers.
   */
  ArrayList getSortedImgReff(File f) {
    ImageRetriever imgRetriever = new ImageRetriever(f)
    imgRetriever.configureImageCollections(this.imgCollections)
    imgRetriever.mu.img =  this.imgSvc
    return imgRetriever.getImageRefList()
  }


  /** Given a set of files with contents in citedown, writes 
   * a parallel set of identically named files substituting
   * references to locally downloaded images for any URN
   * quotations of images.
   * @param fileList List of files to analyze.
   * @param targetDir A writable directory where filtered files
   * will be written.
   * @returns A list of filtered files.
   * @throws Exception if target is not writable.
   */
  ArrayList rewriteImageReff(ArrayList fileList, File targetDir) 
  throws Exception {
    if (!targetDir.exists()) {
      targetDir.mkdir()
    }
    if (!targetDir.canWrite()) {
      throw new Exception("SiteBuilder:rewriteImageReff: cannot write to output directory ${targetDir}")
    }

    ArrayList filteredFiles = []
    Integer imgCount = 1
    fileList.each { f ->
      File targetFile = new File(targetDir, f.name)
      filteredFiles.add(targetFile)
      ArrayList reff = getSortedImgReff(f)
      ArrayList lineList = f.readLines()
      reff.each { ref ->
	java.util.regex.Pattern citePattern = ~/\{([^{]+)\}\[${ref}\]/
			       
	lineList.eachWithIndex { l, i ->
	  // Check for reference definition:
	  if (l.startsWith("[${ref}]:")) {
	    String revision = l.replaceFirst(/:.+/, ": images/img${imgCount}.jpg")
	    lineList[i]  = revision
	  }
	  // And for reference quotation:
	  java.util.regex.Matcher matcher = l =~ citePattern
	  if (matcher.getCount()) {
	    String revision = l.replaceAll(citePattern) { full, caption ->
	      return "[${caption}][${ref}]"
	    }
	    lineList[i] = revision
	  }
	}
	imgCount++;
      }
      
      lineList.each {
	targetFile.append(it + "\n")
      }
    }
    return filteredFiles
  }


  /** Recursively walks directory tree and collects filenames in
   * proper order.
   * Checks for presence of toc file, and, if present, uses its sequence.
   * Otherwise, follows alphabetical order.
   * @param dir Root directory to begin from.
   * @returns Ordered list of file names.
   */
  java.util.ArrayList sequenceFiles(File dir) {
    return sequenceFiles(dir,[])
  }

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
    } else {
      return getFileNamesAlphabetically(dir,fileList)
    }
  }

  /** For a directory with a toc.txt file, collects all file names, 
   * and recursively sequences any listed subdirectories.
   * @param dir Directory to analyze.
   * @param files Ordered list of previously collected files.
   * @returns An ordered List of File objects.
   * @throws Exception if toc.txt does not exist in dir, or if
   * any files listed in toc.txt do not exist.
   */
  java.util.ArrayList getFileNamesByToc(File dir, ArrayList files) 
  throws Exception {
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


  String getArchiveRoot(){
    return FilenameUtils.getPath("${this.fileSequence[0]}")
  }

  void web(File targetDir) 
  throws Exception {
    if (!targetDir.exists()) {
      targetDir.mkdir()
    }
    if (!targetDir.canWrite()) {
      throw new Exception("SiteBuilder:flatCopy: Cannot write to output directory ${targetDir}")
    } 
    
    Web web = new Web(getArchiveRoot(), targetDir)
    this.fileSequence.eachWithIndex {  f, i ->
      // IDfy directory we're in.

      // - check for web.properties, and configure if needed
      WebConfig conf = new WebConfig()

      // - compute uplink.  Need to find first file in parent dir.
      // Do this once and keep a hash of directories -> up link?
      String up = ""

      // - compute title for file
      String title = ""

      String prev = ""
      String next = ""
      if (i > 0) {
	prev = "${fileSequence[i - 1]}"
      }
      if (i < (fileSequence.size() - 1)) {
	next = "${fileSequence[i + 1]}"
      }


      String pageContent = web.formatPage(f.getText(inputEncoding), title, conf, up, prev, next)
      System.err.println "Write html to " + web.htmlForCd(f)
    }
  }


  /** Produces a unique name for a file to copy to a
   * given directory.  If the file's name is not already
   * taken, it simply returns the source file's name.
   * Otherwise, it appends an integer to the file's name.
   * @param dir Target directory where file should be copied.
   * @param fileName Source file to copy to dir.
   * @returns A String guaranteed not to conflict with
   * any file names in dir.
   */
  String getNewFileName(File dir, String fileName) {
    String newName

    // Use current name if it doesn't already exist
    File simpleSolution = new File (dir, fileName)
    if (! simpleSolution.exists()) {
      newName = fileName


    // but if it does, use Apple-like rename with integer appended:
    } else {
      String baseName =  org.apache.commons.io.FilenameUtils.getBaseName(fileName)
      String extension =   org.apache.commons.io.FilenameUtils.getExtension(fileName)
      boolean hasExtension = extension.size() > 0
      
      Integer count = 1
      boolean done = false
      while (! done ) {
	String candidateName
	// courteous to preserve extensions for source files that have
	// have them:
	if (hasExtension) {
	  candidateName = "${baseName}-${count}.${extension}"
	} else {
	  candidateName = "${fileName}-${count}"
	}
	File testFile = new File(dir,candidateName)
	if (testFile.exists()) {
	  count++;
	} else {
	  newName =  candidateName
	  done = true
	}
      }
    }
    return newName
  }


  /** Copies to outputDir files in this archive in the sequence
   * determined in the constructor. Checks for duplicate names. Writes
   * ordered list of files to Books.txt in outputDir for use with leanpub.
   * @param fileList Ordered list of files to copy.
   * @param outputDir Writable directory for output.
   * @returns An ordered list of the new Files.
   * @throws Exception if outputDir is not writable,
   * or if a file listed in the file sequence is not readable.
   */
  ArrayList flatCopy(File outputDir) 
  throws Exception {
    return flatCopy(this.fileSequence, outputDir)
  }

  /** Copies files listed in ordered list fileList to outputDir, and returns
   * an ordered list of output files.  Checks for duplicate names. Writes
   * ordered list of files to Books.txt in outputDir for use with leanpub.
   * @param fileList Ordered list of files to copy.
   * @param outputDir Writable directory for output.
   * @returns An ordered list of the new Files.
   * @throws Exception if outputDir is not writable,
   * or if a file in fileList is not readable.
   */
  ArrayList flatCopy(ArrayList fileList, File outputDir) 
  throws Exception {
    if (!outputDir.exists()) {
      outputDir.mkdir()
    }
    if (!outputDir.canWrite()) {
      throw new Exception("SiteBuilder:flatCopy: Cannot write to output directory ${outputDir}")
    } 
   
    File leanpub = new File(outputDir, "Books.txt")
    File bfdocsManifest = new File(outputDir,"manifest.json")

    // ordered list of files:
    ArrayList outputSequence = []
    ArrayList fileNames = []
    fileList.each { f ->
      if (f.exists() && f.canRead()) {
	String fileName = getNewFileName(outputDir, f.name)
	File dest = new File(outputDir, fileName)
	Files.copy(f, dest) 
	outputSequence.add(dest)
	fileNames.add(dest.name)
	leanpub.append("${dest.name}\n", outputEncoding)
	
      } else {
	throw new Exception("SiteBuilder:flatCopy: cannot read file ${f}.")
      }
    }

    String jsonManifest = writeBfDocsManifest(fileNames)
    bfdocsManifest.setText(jsonManifest, outputEncoding)
    return outputSequence
  }

  String writeBfDocsManifest() {
    return writeBfDocsManifest(this.fileSequence)
  }
  
  String writeBfDocsManifest(ArrayList fileNames) {
    def jsonBldr = new JsonBuilder()
    def root =     jsonBldr {
      title this.archiveTitle
      files fileNames
    }
    return jsonBldr.toString()
  }



  /** Generates a translation of an entire citedown
   * archive in pure markdown that can be directly used with
   * leanpub (https://leanpub.com/).  Organization in subdirectories 
   * is flattened into a single directory with leanpub's Books.txt
   * file listing the contents in the correct order. Quoted images
   * are downloaded in a subdirectory named "images", and references 
   * in the markdown files are changed to point to the local images.
   * @param targetDir A writable directory where output will be
   * written.  If this is the "manuscripts" directory of a leanpub
   * project, you can directly publish your work from leanpub's
   * web interface.
   */
  void flatmd(File targetDir) 
  throws Exception {
    if (!targetDir.exists()) {
      targetDir.mkdir()
    }
    if (!targetDir.canWrite()) {
      throw new Exception("SiteBuilder:flatCopy: Cannot write to output directory ${targetDir}")
    } 
    
    File flattened = new File("${targetDir}/TEMPDIR-flattened")
    flattened.mkdir()
    
    File filtered = new File("${targetDir}/TEMPDIR-filtered")
    filtered.mkdir()


    ArrayList flatList = this.flatCopy(flattened)
    if (debug > 0) {
      System.err.println "Flattened source files into ${flattened}"
    }
    
    ArrayList modifiedList = this.rewriteImageReff(flatList, filtered)
    if (debug > 0) {
      System.err.println "Filtered flattened source files into ${filtered}"
    }

    ArrayList leanpubMarkdown = this.cdToMd(modifiedList,targetDir)

    if (debug > 0) {
      System.err.println "Filtered flattened source files into ${filtered}"
    }

    File imgDir = new File("${targetDir}/images")
    this.retrieveImages(imgDir)


    // Books.txt and manifest.json are made here:
    // move them to ultimate output dir:
    File books = new File(targetDir,"Books.txt")
    books.setText(new File(flattened, "Books.txt").getText(inputEncoding), outputEncoding)


    File manifest = new File(targetDir,"manifest.json")
    manifest.setText(new File(flattened, "manifest.json").getText(inputEncoding), outputEncoding)

    if (debug > 1) {
    } else {
      flattened.deleteDir()
      filtered.deleteDir()
    }
  }


  /** Uses a MarkdownUtil to convert citedown source text to
   * pure markdown.
   * @param f File with contents in citedown.
   * @returns A String of pure markdown.
   */
  String convertToMarkdown(File f) {
    if (debug > 0) {
      System.err.println "SiteBuider:convertToMarkdown: Convert contents of ${f} to pure markdown string"
    }
    MarkdownUtil mdu = new MarkdownUtil(f.getText())
    mdu.cts = this.cts
    mdu.img = this.imgSvc
    mdu.imgCollections = this.imgCollections
    return mdu.toMarkdown()
  }

  /** Given a set of files with content in citedown,
   * writes a matching set of identically named files 
   * converted to pure markdown.
   * @param srcFiles A list of files to convert.
   * @param outDir A writable directory where converted
   * files should be written
   * @returns
   * @throws Exception if cannot write to outDir.
  */
  ArrayList cdToMd(ArrayList srcFiles, File outDir) 
  throws Exception{
    if (!outDir.exists()) {
      outDir.mkdir()
    }
    if (!outDir.canWrite()) {
      throw new Exception("SiteBuilder:cdToMd: Cannot write to output directory ${outDir}")
    } 


    ArrayList mdFiles = []
    srcFiles.each { f ->
      File target = new File(outDir, f.name)
      if (debug > 0) { System.err.println "SiteBuilder:cdToMd: Convert citedown file ${f}  to markdown ${target}" }
      target.setText(convertToMarkdown(f), outputEncoding)
      mdFiles.add(target)
    }
    return mdFiles
  }

}

