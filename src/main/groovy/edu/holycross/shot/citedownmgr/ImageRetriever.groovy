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
*/
class ImageRetriever {


  Integer debug = 0

  MarkdownUtil mu


  ImageRetriever(File mdFile) 
  throws Exception {
    String md = mdFile.getText()
    this.mu = new MarkdownUtil(md)
    mu.collectReferences()
  }

  ImageRetriever(String mdSource) 
  throws Exception {
    this.mu = new MarkdownUtil(mdSource)
    this.mu.collectReferences()
  }


  void configureImageCollections(ArrayList imgColls) {
    this.mu.imgCollections = imgColls
  }

  // check that mu.img is configured, and that
  // mu.imgCollections is configured
  // Return GetBinaryImage request URL for retrieval of binary
  // image  data
  String urlForUrn(CiteUrn urn) {
    return "${mu.img}?request=GetBinaryImage&urn=${urn}"
  }


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


  
  Integer retrieveImages(File outputDir, Integer imgNum) {
    Integer numberFound = 0
    if (! outputDir.exists()) {
      outputDir.mkdir()
    }
    this.mu.referenceMap.keySet().sort().each { ref ->
      def mapping =  this.mu.referenceMap[ref]
      String urnStr = mapping[0]
      CiteUrn urn
      try {
	urn = new CiteUrn(urnStr)
      } catch (Exception e) {
	if (debug > 0) {
	  System.err.println "ImageRetriever:retrieveImages: skip reference ${urnStr}"
	}
      }

      if (urn) {
	String collUrn = "urn:cite:${urn.getNs()}:${urn.getCollection()}"
	if (this.mu.imgCollections.contains(collUrn)) {
	  String urlStr = urlForUrn(urn)
	  File imgFile = new File(outputDir, "img${imgNum}.jpg")
	  this.download(urlStr, imgFile)
	  imgNum++;
	  numberFound++;
	}
      }
    }
    return numberFound
  }

}

