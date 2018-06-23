package com.viettel.ocs.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/***
 * Xml Utils
 * @author huannn
 *
 */
public class XMLUtil {
	
	public static Document getDoc(String filePath) throws ParserConfigurationException, SAXException, IOException {
		
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		return doc;
	}

	public static List<Element> getListElement(Node node) {
		
		List<Element> lst = new ArrayList<Element>();		
		NodeList lstChildren = node.getChildNodes();
		for(int j = 0; j < lstChildren.getLength(); j++) {
			
			Node childNode = lstChildren.item(j);
			if(childNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element ele = (Element) childNode;
				lst.add(ele);
			}			
		}
		
		return lst;		
	}
}
