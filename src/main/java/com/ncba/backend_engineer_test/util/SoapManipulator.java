package com.ncba.backend_engineer_test.util;



import com.ncba.backend_engineer_test.dto.CountryResponse;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SoapManipulator {
    private final Document doc;

    public SoapManipulator(String xmlResponse) throws IllegalArgumentException {
        try {
            // prep document
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(xmlResponse));
            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            df.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
            df.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
            DocumentBuilder builder = df.newDocumentBuilder();
            doc = builder.parse(src);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Error parsing xml response");
        }
    }



    public String getAccountTagValue(String tagName , int index) {
        try {
            return doc != null ? doc.getElementsByTagName(tagName).item(index).getTextContent() : "";
        } catch (Exception e) {
            return "";
        }
    }

    public String getTagValue(String tagName) {
        try {
            return doc != null ? doc.getElementsByTagName(tagName).item(0).getTextContent() : "";
        } catch (Exception e) {
            return "";
        }
    }



    // Helper to safely read a child tag's text from a specific country element
    private String getChildText(org.w3c.dom.Element parent, String childTag) {
        try {
            NodeList nodes = parent.getElementsByTagName(childTag);
            if (nodes != null && nodes.getLength() > 0) {
                String text = nodes.item(0).getTextContent();
                return text != null ? text : "";
            }
        } catch (Exception ignored) { }
        return "";
    }

    public List<CountryResponse> getCustomerAccounts(String tagName) {
        ArrayList<CountryResponse> linkedAccountsList = new ArrayList<>();
        NodeList messagesNodeList = doc.getElementsByTagName(tagName);
        for (int i = 0; i < messagesNodeList.getLength(); i++) {
            org.w3c.dom.Element countryEl = (org.w3c.dom.Element) messagesNodeList.item(i);

            CountryResponse accountDetails = new CountryResponse();
            // Read fields from within this specific country element to avoid cross-index mixups
            accountDetails.setCapital(getChildText(countryEl, "m:sCapitalCity"));
            accountDetails.setCurrency(getChildText(countryEl, "m:sCurrencyISOCode"));
            accountDetails.setName(getChildText(countryEl, "m:sName"));
            accountDetails.setContinent(getChildText(countryEl, "m:sContinentCode"));
            accountDetails.setIsoCode(getChildText(countryEl, "m:sISOCode"));

            // Build comma-separated languages from nested m:Languages -> m:tLanguage -> m:sName (fallback to m:sISOCode)
            String languagesCsv = "";
            try {
                NodeList langsNodes = countryEl.getElementsByTagName("m:Languages");
                if (langsNodes != null && langsNodes.getLength() > 0) {
                    org.w3c.dom.Element langsEl = (org.w3c.dom.Element) langsNodes.item(0);
                    NodeList tLangs = langsEl.getElementsByTagName("m:tLanguage");
                    java.util.List<String> langs = new java.util.ArrayList<>();
                    for (int j = 0; j < tLangs.getLength(); j++) {
                        org.w3c.dom.Element langEl = (org.w3c.dom.Element) tLangs.item(j);
                        String name = "";
                        try {
                            NodeList nameNodes = langEl.getElementsByTagName("m:sName");
                            if (nameNodes != null && nameNodes.getLength() > 0) {
                                name = nameNodes.item(0).getTextContent();
                            }
                            if (name == null || name.isBlank()) {
                                NodeList isoNodes = langEl.getElementsByTagName("m:sISOCode");
                                if (isoNodes != null && isoNodes.getLength() > 0) {
                                    name = isoNodes.item(0).getTextContent();
                                }
                            }
                        } catch (Exception ignore) { }
                        if (name != null && !name.isBlank()) {
                            langs.add(name.trim());
                        }
                    }
                    languagesCsv = String.join(", ", langs);
                }
            } catch (Exception e) {
                // ignore and keep empty languagesCsv
            }
            accountDetails.setLanguage(languagesCsv);
            linkedAccountsList.add(accountDetails);
        }
        return linkedAccountsList;
    }
}