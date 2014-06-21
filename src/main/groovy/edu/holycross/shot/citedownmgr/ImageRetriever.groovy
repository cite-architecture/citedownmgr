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


/** A class to support working with citedown content including quotation
 * of images.
*/
class ImageRetriever {


  Integer debug = 0

  Integer  srcWidth = 9000


  /** An instance of the utility class 
   * that can parse and work with citedown.
   */
  MarkdownUtil mu


  /** Constructor to work with image references
   *  in a file.
   * @param mdFile A file with content in citedown.
   */
  ImageRetriever(File mdFile) 
  throws Exception {
    String md = mdFile.getText()
    this.mu = new MarkdownUtil(md)
    mu.collectReferences()
  }


  /** Constructor to work with image references
   *  in from a String source.
   * @param mdSource A String of content in citedown.
   */
  ImageRetriever(String mdSource) 
  throws Exception {
    this.mu = new MarkdownUtil(mdSource)
    this.mu.collectReferences()
  }



  /** Configures markdown utility to recognize 
   * a set of Cite Collections as image collections.
   * @param imgColls List of collection-level CITE URNs
   * identifying image collections.
   */
  void configureImageCollections(ArrayList imgColls) {
    this.mu.imgCollections = imgColls
  }



  // check that mu.img is configured, and that
  // mu.imgCollections is configured
  // Return GetBinaryImage request URL for retrieval of binary
  // image  data
  String urlForUrn(CiteUrn urn) {
    return "${mu.img}?request=GetBinaryImage&w=${srcWidth}&urn=${urn}"
  }


  /** Retrieves binary image content from a remote
   * and saves locally.
   * @param address The URL, as a String, of an image
   * to retrieve.
   * @param imgFile A local file for the retrieved image.
   */
  void download(String address, File imgFile) {
    imgFile.withOutputStream { out ->
      out << new URL(address).openStream()
    }
  }


  // resolve URNs to URLs, retrieve images,
  // save to writable dir outputDir
  // reports number of images downloaded
  Integer retrieveImages(File outputDir) {
    return retrieveImages(outputDir, 1)
  }

  // get citedown references that refer to 
  // images in configured collections.
  // criteria:  must be a valid CITE Collection
  // URN in a configured collection
  ArrayList getImageRefList() {
    ArrayList imgReff = []
    this.mu.collectReferences()
    this.mu.referenceMap.keySet().sort().each { ref ->
      def mapping =  this.mu.referenceMap[ref]
      String urnStr = mapping[0]
      CiteUrn urn
      try {
	urn = new CiteUrn(urnStr)
      } catch (Exception e) {
	if (debug > 1) {
	  System.err.println "ImageRetriever:retrieveImages: skip reference ${urnStr}"
	}
      }

      if (urn) {
	if (debug > 2) {
	  System.err.println "\nImageRetriever:retrieveImages: check ${urn} in collection..."
	}

	def collUrn = "urn:cite:${urn.getNs()}:${urn.getCollection()}"
	this.mu.imgCollections.each { c ->
	  if  (c.toString() == collUrn) {
	    imgReff.add(ref)
	  }
	}
      }
    }
    return imgReff.sort()
  }


  // excpetion if outputDir is not writable
  Integer retrieveImages(File outputDir, Integer imgNum) 
  throws Exception {
    Integer numberFound = 0
    if (! outputDir.exists()) {
      outputDir.mkdir()
    }
    if (! outputDir.canWrite()) {
      throw new Exception("ImageRetriever:retrieveImages: cannot save images to ${outputDir}")
    }

    if (debug > 0) {
      System.err.println "\nImageRetriever:retrieveImages: will put images in ${outputDir}\n"
    }
    ArrayList imgReff = this.getImageRefList()
    if (debug > 0) {
      System.err.println "ImageRetriever:retrieveImages: list of Reff = " + imgReff
    }
    imgReff.each { ref ->
      def mapping =  this.mu.referenceMap[ref]
      String urnStr = mapping[0]
      CiteUrn urn = new CiteUrn(urnStr)
      
      def collUrn = "urn:cite:${urn.getNs()}:${urn.getCollection()}"
      this.mu.imgCollections.each { c ->
	if  (c.toString() == collUrn) {
	  String urlStr = urlForUrn(urn)
	  File imgFile = new File(outputDir, "img${imgNum}.jpg")
	  this.download(urlStr, imgFile)
	  imgNum++;
	  numberFound++;
	}
      }
    }
    assert numberFound == imgReff.size()
    return numberFound
  }
  
}

