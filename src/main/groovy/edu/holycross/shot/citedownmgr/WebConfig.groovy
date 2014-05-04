package edu.holycross.shot.citedownmgr

import java.util.Properties


/** Utility class to manage settings for laying out
* an HTML5 web page.
*/
class WebConfig {

    /** Contents to insert withint the HTML head element.
    * Note specifically that this could include links to CSS,
    * or XSLT stylesheets.
    */
    String htmlHead = "<script type='text/javascript' src='http://folio.furman.edu/citekit/js/cite-jq.js'></script>\n"
    /** Contents of the HTML5 header element, with role = 'banner'.
    */
    String html5Header
    /** Contents of the HTML5 footer element.
    */
    String html5Footer
    /** Contents of an HTML5 nav element, with role = 'navigation'.
    */
    String html5Nav

    
    /** Empty constructor initializes all values to empty string.*/
    WebConfig() {
        this.htmlHead = ""
        this.html5Header = ""
        this.html5Footer= ""
        this.html5Nav = ""
    }

    /** Constructor from a Java properties file.
    * @param f A file, in Java properties format (key=value),
    * with configuration values to use.  Recognized property
    * names are head, header, footer and nav.
    * @throws Exception if f is not readable.
    */
    WebConfig(File f) 
    throws Exception {
        if (!f.canRead()) {
            throw new Exception("Cannot read configuration file ${f}")
        } else {
            FileInputStream fis = new FileInputStream(f)
            Properties props = new Properties()
            props.load(fis)
            updateSettings(props)
        }
    }


    /** Updates values of configuration settings from a Java
    * properties file.  The Java property names it checks
    * for are head, header, footer and nav.  If any of these are not
    * contained in the properties object, the value of the corresponding
    * WebConfig setting remains unchanged.
    * @param propertyFile.  The properties file with new values to 
    * override current settings.
    */
    void updateSettings(File propertyFile) {
        FileInputStream fis = new FileInputStream(propertyFile)
            Properties props = new Properties()
            props.load(fis)
            updateSettings(props)
    }

    /** Updates current configuration settings from values in
    * a Java Properties object.  The Java property names it checks
    * for are head, header, footer and nav.  If any of these are not
    * contained in the properties object, the value of the corresponding
    * WebConfig setting remains unchanged.
    * @param properties The properties object with new values to override
    * current settings.
    */
    void updateSettings(Properties properties) {
        this.htmlHead = properties.getProperty("head",this.htmlHead)
        this.html5Header = properties.getProperty("header",this.html5Header)
        this.html5Footer = properties.getProperty("footer",this.html5Footer)
        this.html5Nav = properties.getProperty("nav",this.html5Nav)
    }


    /** Overrides default Object toString method.
    */
    String toString() {
        return """
head=${this.htmlHead}

header=${this.html5Header}
footer=${this.html5Footer}
nav=${this.html5Nav}
"""
    }
    

}
