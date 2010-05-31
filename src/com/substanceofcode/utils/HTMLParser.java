/*
 * HTMLParser.java
 *
 * Copyright (C) 2007-2008 Tommi Laukkanen
 * Copyright (C) 2007-2008 Irving Bunton
 * http://www.substanceofcode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
/*
 * IB 2010-04-09 1.11.5RC2 Change REDIRECT_URL to be after the last XmlParser token.
 * IB 2010-04-30 1.11.5RC2 Use absolute address for redirects.
 * IB 2010-04-30 1.11.5RC2 Fixed problem with end tags not recognized if spaces are inside by making changes in XmlParser and calling getTextStream from there.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser in small memory MIDP 1.0 to save space.
 * IB 2010-05-28 1.11.5RC2 Check for html, htm, shtml, and shtm suffixes.
*/

// Expand to define memory size define
//#define DREGULARMEM
// Expand to define logging define
//#define DNOLOGGING
//#ifndef DSMALLMEM
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif
/**
 * Simple and lightweight HTML parser without complete error handling.
 *
 * @author Irving Bunton
 */
public class HTMLParser extends XmlParser {
    
	private boolean m_encodingSet = false;
	private boolean m_headerFound = false;
	private boolean m_metaFound = false;
	private boolean m_bodyFound = false;
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("HTMLParser");
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
	//#endif
	private String m_redirectUrl = "";
	final private String m_url;
    
    /** Enumerations for parse function */
    public static final int REDIRECT_URL = LAST_TOKEN + 1;

    /** Creates a new instance of HtmlParser
     * IB 2010-04-30 1.11.5RC2 Use absolute address for redirects.
	 */
    public HTMLParser(String url, InputStream inputStream) {
		super(inputStream);
		m_url = url;
		m_defEncoding = "ISO-8859-1";
    }

    /** Creates a new instance of HtmlParser
     * IB 2010-04-30 1.11.5RC2 Use absolute address for redirects.
	 */
    public HTMLParser(String url, EncodingUtil encodingUtil) {
		super(encodingUtil);
		m_url = url;
		m_defEncoding = "ISO-8859-1";
    }

    /** Parse next element
     * IB 2010-04-30 1.11.5RC2 Use absolute address for redirects.
	 */
    protected int parseStream(InputStreamReader is) throws IOException {
		int elementType = super.parseStream(is);
		if (elementType != XmlParser.ELEMENT) {
			return elementType;
		}
		if (m_bodyFound) {
			return elementType;
		} else if (m_headerFound) {
			String elementName = super.getName();
			switch (elementName.charAt(0)) {
				case 'b':
				case 'B':
					m_bodyFound = elementName.toLowerCase().equals("body");
					// Default HTML to iso-8859-1
					if (m_bodyFound && !m_encodingSet) {
						//#ifdef DLOGGING
//@						if (finerLoggable) {logger.finer("Body found without encoding set.");}
						//#endif
						m_encodingUtil.getEncoding(m_fileEncoding,
								"ISO-8859-1");
						m_docEncoding = m_encodingUtil.getDocEncoding();
						m_encodingSet = true;

						//#ifdef DLOGGING
//@						if (finerLoggable) {logger.finer("Body found m_docEncoding,m_fileEncoding=" + m_docEncoding + "," + m_fileEncoding);}
						//#endif
					}
					break;
				case 'm':
				case 'M':
					m_metaFound = elementName.toLowerCase().equals("meta");
					if (m_metaFound) {
						//#ifdef DLOGGING
//@						if (finerLoggable) {logger.finer("Parsing <meta> tag");}
						//#endif
						String httpEquiv;
						if (((httpEquiv = getAttributeValue( "http-equiv" ))
									== null) || ( httpEquiv.length() == 0 )) {
							break;
						}
						String content;
						if (((content = getAttributeValue( "content" ))
									== null) || ( content.length() == 0 )) {
							break;
						}
						int pcharset = content.toLowerCase().indexOf(
								"charset=");
						if (pcharset >= 0) {
							String encoding = content.substring(pcharset + 8);
							//#ifdef DLOGGING
//@							if (finerLoggable) {logger.finer("encoding=" + encoding);}
							//#endif
							m_encodingUtil.getEncoding(m_fileEncoding,
									encoding);
							m_docEncoding = m_encodingUtil.getDocEncoding();
							m_encodingSet = true;
						} else {
							int plink = content.toLowerCase().indexOf("url=");
							if (plink < 0) {
								break;
							}
							String link = content.substring(plink + 4);
							if (link.length() > 0) {
								try {
									m_redirectUrl = HTMLParser.getAbsoluteUrl(
											m_url, link);
								} catch (IllegalArgumentException e) {
									IOException ioe = new IOException(
											"Unable to redirect bad url " +
											e.getMessage());
									//#ifdef DLOGGING
//@									logger.severe(ioe.getMessage(), ioe);
									//#endif
									throw ioe;
								}
								//#ifdef DLOGGING
//@								if (finerLoggable) {logger.finer("m_redirectUrl=" + m_redirectUrl);}
								//#endif
								return REDIRECT_URL;
							}
						}
					}
					break;
				default:
			}
		} else if (!m_headerFound) {
			String elementName = super.getName();
			switch (elementName.charAt(0)) {
				case 'h':
				case 'H':
					m_headerFound = elementName.toLowerCase().equals("head");
					//#ifdef DLOGGING
//@					if (finerLoggable && m_headerFound) {logger.finer("m_headerFound=" + m_headerFound);}
					//#endif
					break;
				default:
			}

		}
		return elementType;
    }
    
    /** Parse next element */
    public int parse() throws IOException {
		if (m_encodingStreamReader.isModEncoding()) {
			return parseStream(m_encodingStreamReader);
		} else {
			return parseStream(m_inputStream);
		}
	}
		
    /** 
     * Get attribute value from current element 
     */
    public String getAttributeValue(String attributeName) {
        
		try {
			/** Check whatever the element contains given attribute */
			String ccurrentElementData = EncodingUtil.replaceSpChars(
					EncodingUtil.replaceSpChars(
						m_currentElementData.toString(), true, false),
					false, false);
			int attributeStartIndex = ccurrentElementData.toLowerCase().indexOf(
					" " + attributeName.toLowerCase());
			if( attributeStartIndex<0 ) {
				return null;
			}
			
			/** Calculate actual value start index */
			int valueStartIndex = attributeStartIndex +
					attributeName.length() + 1;
			String attribData = ccurrentElementData.substring(
					valueStartIndex).trim();
			if (attribData.length() == 0) {
				return null;
			}
			String quote = null;
			if (attribData.charAt(0) == '=') {
				attribData = attribData.substring(1).trim();
				if (attribData.length() == 0) {
					return null;
				}
			}
			switch (attribData.charAt(0)) {
				case '\"':
					attribData = attribData.substring(1);
					if (attribData.length() == 0) {
						return null;
					}
					quote = "\"";
					break;
				case EncodingUtil.CLEFT_SGL_QUOTE:
					attribData = attribData.substring(1);
					quote = EncodingUtil.RIGHT_SGL_QUOTE;
					if (attribData.length() == 0) {
						return null;
					}
					break;
				case EncodingUtil.CWLEFT_SGL_QUOTE:
					attribData = attribData.substring(1);
					if (attribData.length() == 0) {
						return null;
					}
					quote = EncodingUtil.WRIGHT_SGL_QUOTE;
					break;
				default:
			}
			
			/** Check the attribute value end index */
			int valueEndIndex;
			if (quote != null) {
				valueEndIndex = attribData.indexOf(quote);
			} else {
				attribData = attribData.trim();
				valueEndIndex = attribData.indexOf(' ');
				if( valueEndIndex<0 ) {
					valueEndIndex = attribData.length();
				}
				int lpos = attribData.indexOf('>');
				if (lpos > 0) {
					if (valueEndIndex > 0) {
						valueEndIndex = Math.min(lpos, valueEndIndex);
					} else {
						valueEndIndex = lpos;
					}
				}
			}

			if( valueEndIndex<=0 ) {
				return null;
			}
			
			/** Parse value */
			String value = attribData.substring(0, valueEndIndex);
			if (m_docEncoding.length() != 0) {
				// We read the bytes in as ISO8859_1, so we must get them
				// out as that and then encode as they should be.
				if (m_fileEncoding.length() == 0) {
					value = new String(value.getBytes(),
									  m_docEncoding);
				} else {
					value = new String(value.getBytes(
								m_fileEncoding), m_docEncoding);
				}
			}
					
			return value;
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("getAttributeValue error.", t);
//#endif
			System.out.println("getAttributeValue error." + t + " " +
					           t.getMessage());
			return null;
		}
    }

	static public String getAbsoluteUrl(String  url, String link)
	throws IllegalArgumentException {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("HTMLParser");
		//#endif
		link = link.trim();
		if (link.indexOf("://") >= 0) {
			if (link.startsWith("http:") ||
				link.startsWith("https:") ||
				link.startsWith("file:") ||
				 link.startsWith("jar:")) {
				return link;
			} else {
				IllegalArgumentException e =
					new IllegalArgumentException(
					"Not support for protocol or no protocol link=" +
					link);
				//#ifdef DLOGGING
//@				logger.fine(e.getMessage(), e);
				//#endif
				throw e;
			}
		} else {
			if ((link.length() == 0) || (link.charAt(0) != '/')) {
				link = url + "/" + link;
			} else {
				int purl = url.indexOf("://");
				if ((purl + 4) >= url.length()) {
					IllegalArgumentException e =
						new IllegalArgumentException(
						"Url too short link,url=" + link + "," + url);
					//#ifdef DLOGGING
//@					logger.fine(e.getMessage(), e);
					//#endif
					throw e;
				}
				int pslash = url.indexOf("/", purl + 3);
				String burl = url;
				if (pslash >= 0) {
					burl = url.substring(0, pslash);
				}
				link = burl + link;
			}
			return link;
		}
	}

    static public boolean isHtml(String contentType) {
		return ((contentType != null) &&
				(contentType.endsWith("html") ||
				contentType.endsWith("htm") ||
				contentType.endsWith("shtml") ||
				contentType.endsWith("shtm")));
	}

    public void setMetaFound(boolean metaFound) {
        this.m_metaFound = metaFound;
    }

    public boolean isMetaFound() {
        return (m_metaFound);
    }

    public void setBodyFound(boolean bodyFound) {
        this.m_bodyFound = bodyFound;
    }

    public boolean isBodyFound() {
        return (m_bodyFound);
    }

    public void setRedirectUrl(String redirectUrl) {
        this.m_redirectUrl = redirectUrl;
    }

    public String getRedirectUrl() {
        return (m_redirectUrl);
    }

}
//#endif
