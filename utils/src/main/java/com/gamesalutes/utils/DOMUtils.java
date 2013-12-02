/*
 * Copyright (c) 2013 Game Salutes.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     Game Salutes - Repackaging and modifications of original work under University of Chicago and Apache License 2.0 shown below
 * 
 * Repackaging from edu.uchicago.nsit.iteco.utils to com.gamesalutes.utils
 * 
 * Copyright 2008 - 2011 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.gamesalutes.utils;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.validation.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Utility methods for working with the DOM API.
 * 
 * @author Justin Montgomery
 * @version $Id: DOMUtils.java 2688 2011-03-08 23:11:38Z jmontgomery $
 *
 */
public class DOMUtils 
{

	/**
	 * JAXP schema language attribute name
	 */
	public static final String JAXP_SCHEMA_LANG =
		"http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	/**
	 * XML schema language attribute value
	 */
	public static final String W3C_XML_SCHEMA =
		 "http://www.w3.org/2001/XMLSchema";
	/**
	 * XML schema instance attribute name
	 */
	public static final String XML_SCHEMA_INST =
		"xmlns:xsi";
	/**
	 * XML schema instance attribute value
	 */
	public static final String XML_SCHEMA_INST_VALUE =
		"http://www.w3.org/2001/XMLSchema-instance";
	/**
	 * XML schema file location attribute for non-namespace schemas.
	 * 
	 */
	public static final String XML_DEFAULT_SCHEMA_LOC = 
		"xsi:noNamespaceSchemaLocation";
	
	
	
	/**
	 * Validation type for validating XML input.
	 * 
	 * @author Justin Montgomery
	 *
	 */
	public enum Validation {DTD,SCHEMA};
	
	
	public static boolean useNamespaces = true;
	
	
	/**
	 * Saves the document to file.
	 * 
	 * @param doc the <code>Document</code>
	 * @param filename the file path string
	 * @throws IOException if an error occurs during the write operation
	 */
	public static void saveDocument(Document doc,String filename) throws IOException
	{
		saveDocument(doc,new BufferedWriter(FileUtils.newFileWriter(filename)));
		
	} //end saveDocument
	


        /**
         * Returns the content of <code>doc</code> as a <code>String</code>.
         *
         * @param doc the <code>Document</code>
         */
        public static String documentToString(Node doc)
        {
            StringWriter w = new StringWriter(16 * 1024);
            try
            {
                saveDocument(doc,w);
            }
            catch(IOException e)
            {
                throw new IllegalArgumentException("doc=" + doc);
            }
            return w.toString();
        }
	/**
	 * Saves the document by writing the output to <code>out</code>.<b>
	 * <code>out</code> is closed after invoking this method, even if an
	 * exception is thrown</b>.
	 * 
	 * @param doc the <code>Document</code>
	 * @param out the <code>Writer</code> to save the document to
	 * @throws IOException if an error occurs during the write operation
	 */
	public static void saveDocument(Node doc,Writer out)
		throws IOException
	{
		saveDocument(doc,out,true);
	}
	/**
	 * Saves the document by writing the output to <code>out</code>. If <code>autoClose</code>
	 * is <code>true</code>, then <b>
	 * <code>out</code> is closed after invoking this method, even if an
	 * exception is thrown</b>.
	 * 
	 * @param doc the <code>Document</code>
	 * @param out the <code>Writer</code> to save the document to
	 * @throws IOException if an error occurs during the write operation
	 */
	public static void saveDocument(Node doc,Writer out,boolean autoClose)
		throws IOException
	{
		try
		{
			//construct identity transform
			TransformerFactory f = TransformerFactory.newInstance();
			try
			{
				f.setAttribute("indent-number",4);
			}
			catch(Exception e){}
			Transformer t = f.newTransformer();
			//set properties
			t.setOutputProperty(OutputKeys.INDENT,"yes");
			t.transform(new DOMSource(doc), new StreamResult(out));
		}
		catch(TransformerException e)
		{
			throw new IOException("Unable to save document: " + e.getMessage());
		}
		finally
		{
			if(autoClose && out != null)
			{
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * Replaces all elements that have text content of <code>existingText</code> with
	 * text content <code>newText</code>.
	 * 
	 * @param doc <code>Document</code> to search
	 * @param tagName name of <code>Element</code> tag where replacement is applicable
	 *                or <code>null</code> to include all elements
	 * @param existingText the existing text to replace
	 * @param newText the replacement text
	 */
	public static void replaceElementText(Document doc,String tagName,String existingText,String newText)
	{
		replaceElementText(doc.getDocumentElement(),tagName,existingText,newText);
	}
	
	/**
	 * Replaces all elements that have text content of <code>existingText</code> with
	 * text content <code>newText</code>.
	 * 
	 * @param root <code>Element</code> in DOM tree from which to begin the search
	 * @param tagName name of <code>Element</code> tag where replacement is applicable
	 *                or <code>null</code> to include all elements
	 * @param existingText the existing text to replace
	 * @param newText the replacement text
	 */
	public static void replaceElementText(Element root,String tagName,String existingText,String newText)
	{
		String elmTagName = root.getTagName();
		
		//check if tag names match, if applicable and if the current content
		//equals the expected content
		if((tagName == null || elmTagName.equals(tagName)) &&
				root.getTextContent().trim().equals(existingText))
		{
			//replace the text
			root.setTextContent(newText);
		}
		//call recursively on children
		NodeList children = root.getChildNodes();
		for(int i = 0, len = children.getLength(); i < len; ++i)
		{
			Node n = children.item(i);
			if(n instanceof Element)
				replaceElementText((Element)n,tagName,existingText,newText);
		}
		
	}
	
	/**
	 * Makes a copy of the tree rooted at <code>orig</code>, considering
	 * only types <code>Element</code> and <code>Attr</code>.  Comments and 
	 * other node types are ignored.
	 * 
	 * @param orig <code>Element</code> to copy recursively
	 * @param newOwner <code>Document</code> for which returned <code>Element</code>
	 *                 should be owned by
	 * @param blankTextNodes <code>Set</code> of tag names that should not have any text.
	 *                       This is necessary since the method <code>Element.getTextValue()</code>
	 *                       returns the text content of this node and <i> all of its
	 *                       decendents</i>
	 * @return new <code>Element</code> tree that is copy of <code>orig</code> but
	 *         owned by <code>newOwner</code>
	 */
	public static Element copyElement(Element orig,Document newOwner,Set<String> blankTextNodes)
	{
		String tagName = orig.getTagName();
		Element e = newOwner.createElement(tagName);
		//copy attributes
		NamedNodeMap atts = orig.getAttributes();
		for(int i = 0, len = atts.getLength(); i < len; ++i)
		{
			Attr origAttr = (Attr)atts.item(i);
			Attr newAttr = newOwner.createAttribute(origAttr.getName());
			newAttr.setValue(origAttr.getValue());
			e.setAttributeNode(newAttr);
			
		}
		//copy the text if not a node that shouldn't have text
		//this has to be done since getTextContent returns text content
		//of this node AND its decendants
		if(!blankTextNodes.contains(tagName))
			e.setTextContent(orig.getTextContent());
		
		//copy children recursively
		NodeList children = orig.getChildNodes();
		for(int i = 0, len = children.getLength(); i < len; ++i)
		{
			Node n = children.item(i);
			if(n instanceof Element)
				e.appendChild(copyElement((Element)n,newOwner,blankTextNodes));
		}
		
		return e;
	}
	
	/**
	 * Loads a <code>Document</code> from file specified by <code>file</code>
	 * using the specified <code>validation</code>.  <code>validationSource</code>
	 * is closed before this method exits.
	 * 
	 * @param file file path from which to load the <code>Document</code>
	 * @param validation {@link Validation} type for the document
	 * @param validationSource <code>InputStream</code> from which to load the validation structure
	 * @return the loaded <code>Document</code>
	 * @throws IOException if error occurs during validation or reading from file
	 */
	public static Document loadDocument(String file,Validation validation,InputStream validationSource)
		throws IOException
	{
		try
		{
			return loadDocument(FileUtils.newFileInputStream(file),validation,validationSource);
		}
		catch(IOException e)
		{
			throw new ChainedIOException("Error parsing \"" + file  + "\": "
					+ e.getMessage(),e);
		}
	}
	
	/**
	 * Loads a <code>Document</code> from file specified by <code>file</code>
	 * using the specified <code>validation</code>.
	 * <code>docSource</code> and <code>validationSource</code> are
	 * closed after return of this method.
	 * 
	 * @param docSource <code>inputStream</code> from which to load the <code>Document</code>
	 * @param validation {@link Validation} type for the document
	 * @param validationSource <code>InputStream</code> from which to load the validation structure
	 * @return the loaded <code>Document</code>
	 * @throws IOException if error occurs during validation or reading from file
	 */
	public static Document loadDocument(InputStream docSource,Validation validation,InputStream validationSource)
		throws IOException
	{
        try
        {
        	DocumentBuilderFactory factory = createDocumentBuilderFactory(validation,validationSource);
        	DocumentBuilder b = factory.newDocumentBuilder();
        	b.setErrorHandler(new ErrHandler());
        	
    		Document doc =  b.parse(docSource);
    		//bug in JAXP schema validation where whitespace elements are not ignored
    		//even though schema says they don't exist
    		//must manually remove them
//        	if(validation == Validation.SCHEMA)
        		removeWhitespaceNodes(doc.getDocumentElement());
        	return doc;

        }
        catch(Exception e)
        {
        	throw new ChainedIOException(e.getMessage(),e);
        }
		finally
		{
			if(docSource != null)
				docSource.close();
		}
	}
	
	/**
	 * Creates a new blank <code>Document</code>
	 * using the specified <code>validation</code>.
	 * <code>docSource</code> and <code>validationSource</code> are
	 * closed after return of this method.
	 * 
	 * @param validation {@link Validation} type for the document
	 * @param validationSource <code>InputStream</code> from which to load the validation structure
	 * @return the created <code>Document</code>
	 * @throws IOException if error occurs during validation or reading from file
	 */
	public static Document createDocument(Validation validation,InputStream validationSource)
		throws IOException
	{
        try
        {
	    	DocumentBuilderFactory factory = createDocumentBuilderFactory(
	    			validation,validationSource);
	    	DocumentBuilder b = factory.newDocumentBuilder();
	    	Document doc = b.newDocument();
	    	doc.setXmlVersion("1.0");
	    	
	    	return doc;
        }
        catch(Exception e)
        {
        	throw new ChainedIOException(e.getMessage(),e);
        }
    	
	}
	
	/**
	 * Creates a root document element.
	 * 
	 * @param doc the root document element
	 * @param rootName the name of the root document element to create
	 * @param schemaLocation the location of the XML schema or <code>null</code> if not using an XML schema
	 * 
	 * @return the created root element
	 */
	public static Element createRootDocumentElement(Document doc,String rootName,
			String schemaLocation)
	{
		Element root = doc.createElement(rootName);
		if(schemaLocation != null)
		{
			root.setAttribute(DOMUtils.XML_SCHEMA_INST,DOMUtils.XML_SCHEMA_INST_VALUE);
			root.setAttribute(DOMUtils.XML_DEFAULT_SCHEMA_LOC,schemaLocation);
		}
		
		return root;
	}
	
	private static DocumentBuilderFactory createDocumentBuilderFactory(Validation validation,InputStream validationSource)
		throws IOException
	{
		if(validation == Validation.SCHEMA)
			return createSchemaDocumentBuilderFactory(validationSource);
		else if(validation == Validation.DTD)
			return createDtdDocumentBuilderFactory(validationSource);
		else
			return createNoValidateBuilderFactory();
	}
	
	private static DocumentBuilderFactory createNoValidateBuilderFactory()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(useNamespaces);
		factory.setIgnoringComments(true);
		return factory;
	}
	private static DocumentBuilderFactory createDtdDocumentBuilderFactory(InputStream validationSource)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        factory.setValidating(true);
		factory.setNamespaceAware(useNamespaces);

        return factory;
	}
	private static DocumentBuilderFactory createSchemaDocumentBuilderFactory(InputStream validationSource)
		throws IOException
	{
        //load the schema
	     // create schema by from an XSD file:
	     SchemaFactory jaxp = SchemaFactory.newInstance(DOMUtils.W3C_XML_SCHEMA);
	     Schema schema = null;
	     try
	     {
	    	 schema = jaxp.newSchema(new StreamSource(validationSource));
	     }
	     catch(SAXException e)
	     {
	    	 throw new IOException("Unable to parse \"" + validationSource + "\": " +
	    			 e.getMessage());
	     }
	     finally
	     {
	    	 if(validationSource != null)
	    		 validationSource.close();
	     }
	     
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(useNamespaces);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        
        //DEBUG
        //System.out.println(schema);
        //END DEBUG
        
        factory.setSchema(schema);
        
        return factory;
	} //end loadDocument
	
	/**
	  * Removes whitespace from element content. 
	  * @param e the root element
	  */
	public static void removeWhitespaceNodes(Element e)
	{
	    NodeList children = e.getChildNodes();
	    for (int i = children.getLength() - 1; i >= 0; i--)
	    {
	       Node child = children.item(i);
	       if (child instanceof Text && ((Text) child).getData().trim().length() == 0)
	       {
	          e.removeChild(child);
	       }
	       else if (child instanceof Element)
	          removeWhitespaceNodes((Element) child);
	    }
	}

        /**
         *
         * Creates a new xpath object.
         *
         * @return the <code>XPath</code> object
         * @throws IllegalStateException if xpath could not be created
         */
        public static XPath newXPath()
        {
            try
            {
		XPathFactory xpfactory = XPathFactory.newInstance();
		return xpfactory.newXPath();
            }
            catch(Exception e)
            {
                throw ErrorUtils.initWithCause(new IllegalStateException("Unable to create xpath"), e);
            }
        }

        private static class ErrHandler implements ErrorHandler
	{
		public void error(SAXParseException e) throws SAXException
		{
			throw e;
		}
	
	
		public void fatalError(SAXParseException e) throws SAXException 
		{
			throw e;
		}
	
	
		public void warning(SAXParseException e) {}
	} //end ErrHandler
}
