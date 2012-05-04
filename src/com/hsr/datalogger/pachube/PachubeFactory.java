package com.hsr.datalogger.pachube;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PachubeFactory {

	/**
	 * Creates a feed from a String
	 * @param p Pachube object which is the gateway to the pachube service
	 * @param s String which represents  a feed object, this String should be well formed eeml.
	 * @return Feed manufactured from the String
	 */
	public static Feed toFeed(String key, InputStream s) {
		Feed f = new Feed();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(s);
			doc.getDocumentElement().normalize();
			getMisc(f, doc);
			getData(f, doc);
			getLocation(f, doc);
			f.setCreated(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	/**
	 * Method to retrieve data from eeml
	 * @param f
	 * @param doc
	 * @throws DOMException
	 * @throws PachubeException
	 */
	private static void getMisc(Feed f, Document doc) throws DOMException,
			PachubeException {
		NodeList nodeLst = doc.getElementsByTagName("environment");
		Node c = nodeLst.item(0);

		NamedNodeMap n = c.getAttributes();
		Node h = n.getNamedItem("updated");
		if (h != null) {
			f.setUpdated(h.getNodeValue().trim());
		}
		f.setId((n.getNamedItem("id").getNodeValue().trim()));
		if (c.getNodeType() == Node.ELEMENT_NODE) {

			Element e = (Element) c;

			NodeList fstNmElmntLst = e.getElementsByTagName("title");
			Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			NodeList fstNm = fstNmElmnt.getChildNodes();
			f.setTitle(((Node) fstNm.item(0)).getNodeValue().trim());

			fstNmElmntLst = e.getElementsByTagName("status");
			fstNmElmnt = (Element) fstNmElmntLst.item(0);
			fstNm = fstNmElmnt.getChildNodes();
			f.setStatus(Status.valueOf(((Node) fstNm.item(0)).getNodeValue().trim()));

			fstNmElmntLst = e.getElementsByTagName("description");
			fstNmElmnt = (Element) fstNmElmntLst.item(0);

			if (fstNmElmnt != null) {
				fstNm = fstNmElmnt.getChildNodes();
				h = ((Node) fstNm.item(0));
				f.setDescription(h.getNodeValue().trim());
			}

			fstNmElmntLst = e.getElementsByTagName("website");
			fstNmElmnt = (Element) fstNmElmntLst.item(0);

			if (fstNmElmnt != null) {
				fstNm = fstNmElmnt.getChildNodes();
				try {
					f.setWebsite(new URL(((Node) fstNm.item(0)).getNodeValue().trim()));
				} catch (Exception e3) {

				}
			}

			fstNmElmntLst = e.getElementsByTagName("feed");
			fstNmElmnt = (Element) fstNmElmntLst.item(0);
			fstNm = fstNmElmnt.getChildNodes();
			try {
				f.setFeed(new URL(((Node) fstNm.item(0)).getNodeValue().trim()));
			} catch (Exception e3) {

			}

		}

	}

	/**
	 * Method to retrieve data from eeml
	 * @param f
	 * @param doc
	 * @throws PachubeException
	 */
	private static void getLocation(Feed f, Document doc) throws PachubeException {
		NodeList nodeLst = doc.getElementsByTagName("location");
		Node c = nodeLst.item(0);
		PachubeLocation l = new PachubeLocation();
		NamedNodeMap n;
		if (c != null) {
			n = c.getAttributes();

			Node h = n.getNamedItem("exposure");
			if (h != null) {
				String value = h.getNodeValue().trim();
				if (!value.equalsIgnoreCase(""))
						l.setExposure(Exposure.valueOf(value));
			}

			h = n.getNamedItem("disposition");
			if (h != null) {
				String value = h.getNodeValue().trim();
				if (!value.equalsIgnoreCase(""))
					l.setDisposition(Disposition.valueOf(value));
			}

			h = n.getNamedItem("domain");
			if (h != null) {
				String value = h.getNodeValue().trim();
				if (!value.equalsIgnoreCase(""))
					l.setDomain(Domain.valueOf(value));
			}
			
			if (c.getNodeType() == Node.ELEMENT_NODE) {

				Element e = (Element) c;

				NodeList fstNmElmntLst = e.getElementsByTagName("name");
				Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
				NodeList fstNm;
				if (fstNmElmnt != null) {
					fstNm = fstNmElmnt.getChildNodes();
					l.setName(((Node) fstNm.item(0)).getNodeValue().trim());
				}

				fstNmElmntLst = e.getElementsByTagName("lat");
				fstNmElmnt = (Element) fstNmElmntLst.item(0);
				if (fstNmElmnt != null) {
					fstNm = fstNmElmnt.getChildNodes();
					h = ((Node) fstNm.item(0));
					if (h != null) {
						l.setLat(h.getNodeValue().trim());
					}
				}

				fstNmElmntLst = e.getElementsByTagName("lon");
				fstNmElmnt = (Element) fstNmElmntLst.item(0);
				if (fstNmElmnt != null) {
					fstNm = fstNmElmnt.getChildNodes();
					h = ((Node) fstNm.item(0));
					if (h != null) {
						l.setLon(h.getNodeValue().trim());
					}
				}
				
				fstNmElmntLst = e.getElementsByTagName("ele");
				fstNmElmnt = (Element) fstNmElmntLst.item(0);
				if (fstNmElmnt != null) {
					fstNm = fstNmElmnt.getChildNodes();
					h = ((Node) fstNm.item(0));
					if (h != null) {
						l.setElevation(h.getNodeValue().trim());
					}
				}
			}

		}
		
		f.setLocation(l);

	}

	/**
	 * Method to retrieve data of a feed from the feed's eeml document
	 * @param f
	 * @param doc
	 * @throws PachubeException 
	 * @throws DOMException 
	 * @throws ParseException 
	 */
	private static void getData(Feed f, Document doc) throws DOMException, PachubeException, ParseException {
		NodeList nodeLst = doc.getElementsByTagName("data");
		for (int i = 0; i < nodeLst.getLength(); i++) {
			Node fstNode = nodeLst.item(i);

			if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
				f.addData(PachubeFactory.toDataFromNode(fstNode));
			}

		}
	}
	
	/**
	 * Method to retrieve a datastream from eeml inputstream
	 * @param s
	 * @return
	 */
	public static Data toData(InputStream s){
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(s);
			doc.getDocumentElement().normalize();
			
			Node dataNode = doc.getElementsByTagName("data").item(0);
			
			return PachubeFactory.toDataFromNode(dataNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Method to retrieve a datastream from 
	 * @param n
	 * @return
	 * @throws ParseException
	 */
	private static Data toDataFromNode(Node n) throws ParseException{
		Data d = new Data();
		
		Element fstElmnt = (Element) n;

		d.setId(n.getAttributes().getNamedItem("id").getNodeValue().trim());
		
		NodeList ElmntLst;
		
		if ((ElmntLst = fstElmnt.getElementsByTagName("tag")).getLength() > 0) {
			Element lstNmElmnt;
			for (int i=0; i<ElmntLst.getLength(); i++) {
				lstNmElmnt = (Element) ElmntLst.item(0);
				ArrayList<String> tags = new ArrayList<String>();
				if (lstNmElmnt != null) {
					NodeList fstNm = lstNmElmnt.getChildNodes();
					tags.add(((Node) fstNm.item(0)).getNodeValue().trim());
				}
				d.setTag(tags);
			}
		}
		
		if ((ElmntLst = fstElmnt.getElementsByTagName("current_value")).getLength() > 0) {
			Element lstNmElmnt = (Element) ElmntLst.item(0);
			NodeList lstNm = lstNmElmnt.getChildNodes();
			Node h = ((Node) lstNm.item(0));
			if (h != null)
				d.setValue(h.getNodeValue().trim());
		}

		if ((ElmntLst = fstElmnt.getElementsByTagName("max_value")).getLength() > 0) {
			Element lstNmElmnt = (Element) ElmntLst.item(0);
			NodeList lstNm = lstNmElmnt.getChildNodes();
			Node h = ((Node) lstNm.item(0));
			if (h != null)
				d.setMaxValue(h.getNodeValue().trim());
		}

		if ((ElmntLst = fstElmnt.getElementsByTagName("min_value")).getLength() > 0) {
			Element lstNmElmnt = (Element) ElmntLst.item(0);
			NodeList lstNm = lstNmElmnt.getChildNodes();
			Node h = ((Node) lstNm.item(0));
			if (h != null)
				d.setMinValue(h.getNodeValue().trim());
		}

		if ((ElmntLst = fstElmnt.getElementsByTagName("unit")).getLength() > 0) {
			Element lstNmElmnt = (Element) ElmntLst.item(0);
			if (lstNmElmnt != null){
				NamedNodeMap attr = lstNmElmnt.getAttributes();
				Node h = attr.getNamedItem("symbol");
				d.setSymbol(h.getNodeValue().trim());
				NodeList lstNm = lstNmElmnt.getChildNodes();
				h = ((Node) lstNm.item(0));
				if (h != null) 
					d.setUnit(h.getNodeValue().trim());
			}
		}
		
		if ((ElmntLst = fstElmnt.getElementsByTagName("datapoints")).getLength() > 0) {
			Element lstNmElmnt = (Element) ElmntLst.item(0);
			if (lstNmElmnt != null) {
				NodeList fstNm = lstNmElmnt.getChildNodes();
				for (int j = 0; j < fstNm.getLength(); j++){
					DataPoint dp = new DataPoint();
					Element elmt = (Element)fstNm.item(j); 
					
					String time = elmt.getAttributes().getNamedItem("at").getNodeValue().trim();
					String value = ((Node) elmt.getChildNodes().item(0)).getNodeValue().trim();
					
					dp.setTime(time);
					dp.setValue(value);
					d.addDataPoint(dp);
				}
			}
		}
		
		return d;
	}

	/**
	 * Gets the key from the xml string
	 * @param s
	 * @param targetKeyName
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static String toKey(InputStream s, String targetKeyName) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(s);
		doc.getDocumentElement().normalize();
		NodeList nodeLst = doc.getElementsByTagName("key");
		for (int i=0; i<nodeLst.getLength(); i++){
			Node c = nodeLst.item(i);
			NodeList nL = ((Element)c).getElementsByTagName("label");
			NodeList nLElmt = ((Element) nL.item(0)).getChildNodes();
			String keyName = ((Node) nLElmt.item(0)).getNodeValue().trim();
			if (keyName.equalsIgnoreCase(targetKeyName)){
				nL = ((Element)c).getElementsByTagName("api-key");
				nLElmt = ((Element) nL.item(0)).getChildNodes();
				String key = ((Node) nLElmt.item(0)).getNodeValue().trim();
				return key;
			}
		}
		return null;
	}

	public static String toDataXMLWithWrapper(Data d) {
		String ret = "<eeml xmlns=\"http://www.eeml.org/xsd/0.5.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5.1\" xsi:schemaLocation=\"http://www.eeml.org/xsd/0.5.1 http://www.eeml.org/xsd/0.5.1/0.5.1.xsd\">\n<environment>\n\t";
		ret = ret + PachubeFactory.toDataXML(d) + "\n\t</environment>\n</eeml>";
		return ret;
	}

	private static String toDataXML(Data d) {
		String ret = "";
		ret = "<data id=\"" + d.getId() + "\">\n\t\t";
		for (int i=0; i< d.getTag().size(); i++)
			ret = ret + "<tag>" + d.getTag().get(i) + "</tag>\n\t\t";
		if (d.hasValue())
			ret = ret + "<current_value>" + d.getValue() + "</current_value>\n\t";
		if (d.hasMax())
			ret = ret + "<max_value>" + d.getMaxValue() + "</max_value>\n\t";
		if (d.hasMin())
			ret = ret + "<min_value>" + d.getMinValue() + "</min_value>\n\t";
		ret = ret + "<unit type=\"\" symbol=\"" + (d.hasSymbol()?d.getSymbol():"") + "\">" + d.getUnit() + "</unit>\n\t";
		
		ret = ret + "</data>";
		
		return ret;
	}
	
	public static String toFeedXML(Feed f){
		String ret = "<eeml xmlns=\"http://www.eeml.org/xsd/0.5.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5.1\" xsi:schemaLocation=\"http://www.eeml.org/xsd/0.5.1 http://www.eeml.org/xsd/0.5.1/0.5.1.xsd\">";

		if (f.isCreated()) {
			ret = ret + "\n\t<environment ";
			if (f.getUpdated() != null) {
				ret = ret + "updated=\"" + f.getUpdated() + "\" ";
			}
	
			if ((Object)f.getId() != null) {
				ret = ret + "id=\"" + f.getId() + "\"";
			}
	
			ret = ret + ">\n\t<title>" + f.getTitle() + "</title>\n\t";
			ret = ret + "<feed>" + f.getFeed() + "</feed>\n\t";
			ret = ret + "<status>" + f.getStatus() + "</status>\n\t";
			ret = ret + "<private>" + Boolean.toString(f.isPrivate()) + "</private>\n\t";
			ret = ret + "<description>" + f.getDescription() + "</description>\n\t";
			ret = ret + "<website>" + f.getWebsite() + "</website>\n\t";
			if (f.getLocation() != null) {
				ret = ret + toLocationXML(f.getLocation()) + "\n\t";
			}
	
			for (int i = 0; i < f.getData().size(); i++) {
				if (i == f.getData().size() - 1) {
					ret = ret + PachubeFactory.toDataXML(f.getData().get(i)) + "\n";
				} else {
					ret = ret + PachubeFactory.toDataXML(f.getData().get(i)) + "\n\t";
				}
			}
		} else {
			ret = ret + "\n\t<environment>\n\t ";
			ret = ret + "<title>" + f.getTitle() + "</title>\n\t";
			ret = ret + "<private>" + Boolean.toString(f.isPrivate()) + "</private>\n\t";
			if (f.getLocation() != null) {
				ret = ret + toLocationXML(f.getLocation()) + "\n\t";
			}
		}
		ret = ret + "</environment>\n</eeml>";

		return ret;
	}

	public static String toFeedXMLWithoutData(Feed f){
		String ret = "<eeml xmlns=\"http://www.eeml.org/xsd/0.5.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5.1\" xsi:schemaLocation=\"http://www.eeml.org/xsd/0.5.1 http://www.eeml.org/xsd/0.5.1/0.5.1.xsd\">";

		ret = ret + "\n\t<environment ";
		if (f.getUpdated() != null) {
			ret = ret + "updated=\"" + f.getUpdated() + "\" ";
		}

		if ((Object)f.getId() != null) {
			ret = ret + "id=\"" + f.getId() + "\"";
		}

		ret = ret + ">\n\t<title>" + f.getTitle() + "</title>\n\t";
		ret = ret + "<feed>" + f.getFeed() + "</feed>\n\t";
		ret = ret + "<status>" + f.getStatus() + "</status>\n\t";
		ret = ret + "<private>" + Boolean.toString(f.isPrivate()) + "</private>\n\t";
		ret = ret + "<description>" + f.getDescription() + "</description>\n\t";
		ret = ret + "<website>" + f.getWebsite() + "</website>\n\t";
		if (f.getLocation() != null) {
			ret = ret + toLocationXML(f.getLocation()) + "\n\t";
		}

		ret = ret + "</environment>\n</eeml>";

		return ret;
	}
	
	public static String toLocationXML(PachubeLocation loc){
		String ret = "";
		ret = "<location ";
		
		if(loc.getDomain() != null){
			ret = ret + "domain=\""+loc.getDomain()+"\" ";
		}
		
		if(loc.getExposure() != null){
			ret = ret + "exposure=\""+ loc.getExposure() + "\" ";
		}
		
		if(loc.getDisposition() != null){
			ret = ret + "disposition=\""+ loc.getDisposition() +"\" ";
		}
		ret = ret + ">\n\t\t";
		ret = ret + "<name>"+ loc.getName() + "</name>\n\t\t";
		ret = ret + "<lat>"+ loc.getLat() + "</lat>\n\t\t";
		ret = ret + "<lon>"+ loc.getLon() + "</lon>\n\t\t";
		ret = ret + "<ele>"+ loc.getElevation() + "</ele>\n\t";
		ret = ret + "</location>";
		return ret;
	}
	
	public static String toUserXML(User u){
		String ret = "";
		
		ret = ret + "<user>\n\t";
		ret = ret + "<login>" + u.getUsername() + "</login>\n\t";
		ret = ret + "<password>" + u.getPassword() + "</password>\n\t";
		
		/*ret = ret + "\n\t<user> ";
		ret = ret + "\n\t<about>" + u.getAbout() + "</about>\n\t";
		ret = ret + "<full_name>" + u.getName() + "</full_name>\n\t";
		ret = ret + "<email>" + u.getEmail() + "</email>\n\t";
		ret = ret + "<password>" + u.getPassword() + "</password>\n\t";
		ret = ret + "<time_zone>"+ u.getTimezone() + "</time_zone>\n\t";*/
		
		ret = ret + "<roles>\n\t";
		for (int i = 0; i < u.getRoles().size(); i++) {
			if (i == u.getRoles().size() - 1) {
				ret = ret + "<role>" + u.getRoles().get(i) + "</role>\n";
			} else {
				ret = ret + "<role>" + u.getRoles().get(i) + "</role>\n\t";
			}
		}
		ret = ret + "</roles>\n";
		ret = ret + "</user>";

		return ret;
	}
	
	public static String toDataPointXMLWithWrapper(ArrayList<DataPoint> dpList){
		String ret = "<eeml xmlns=\"http://www.eeml.org/xsd/0.5.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"0.5.1\" xsi:schemaLocation=\"http://www.eeml.org/xsd/0.5.1 http://www.eeml.org/xsd/0.5.1/0.5.1.xsd\">";

		ret = ret + "\n\t<environment>";
		
		ret = ret + "\n\t<data>";
		ret = ret + "\n\t<datapoints>\n\t";
		for (int i = 0; i < dpList.size(); i++){
			if (i == dpList.size() - 1) {
				ret = ret + PachubeFactory.toDataPointXML(dpList.get(i)) + "\n";
			} else {
				ret = ret + PachubeFactory.toDataPointXML(dpList.get(i)) + "\n\t";
			}
		}
		ret = ret + "</datapoints>\n";
		ret = ret + "</data>\n";
		
		ret = ret + "</environment>\n</eeml>";

		return ret;
	}

	private static String toDataPointXML(DataPoint dp) {
		return "<value at=\"" + dp.getTime() + "\">" + dp.getValue() + "</value>";
	}
	
	public static String toKeyXML(APIKey key){
		String s="";
		s += "<key>\n";
		s += "<label>" + key.getLabel() + "</label>\n";
		s += "<private-access>" + (key.hasPrivateAccess()?"yes":"no") + "</private-access>\n";
		s += "<permissions>\n";
		s += "<permission>\n";
		s += "<access-methods>\n";
		if (key.hasFullPermission())
			s += "<access-method>get</access-method>\n<access-method>put</access-method>\n<access-method>post</access-method>\n<access-method>delete</access-method>";
		else 
			s += "<access-method>get</access-method>\n";
		s += "</access-methods>\n";
		s += "<resources>\n";
		s += "<resource>\n";
		s += "<feed-id>" + key.getResource() + "</feed-id>\n";
		s += "</resource>\n";
		s += "</resources>\n";
		s += "</permission>\n";
		s += "</permissions>\n";
		s += "</key>\n";
		
		return s;
	}
}