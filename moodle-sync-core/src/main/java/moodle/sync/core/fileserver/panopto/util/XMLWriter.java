package moodle.sync.core.fileserver.panopto.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XMLWriter {


    public static byte[] CreateUpload_manifest(String title, String description, String date, String filename) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Session");
        rootElement.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttribute("xmlns:xsd","http://www.w3.org/2001/XMLSchema");
        rootElement.setAttribute("xmlns","http://tempuri.org/UniversalCaptureSpecification/v1");
        doc.appendChild(rootElement);

        Element ti = doc.createElement("Title");
        ti.setTextContent(title);
        rootElement.appendChild(ti);

        Element des = doc.createElement("Description");
        des.setTextContent(description);
        rootElement.appendChild(des);

        Element da = doc.createElement("Date");
        da.setTextContent(date);
        rootElement.appendChild(da);

        Element thumb = doc.createElement("ThumbnailTime");
        thumb.setTextContent("PT5S");
        rootElement.appendChild(thumb);

        Element videos = doc.createElement("Videos");
        rootElement.appendChild(videos);

        Element video = doc.createElement("Video");
        videos.appendChild(video);

        Element start = doc.createElement("Start");
        start.setTextContent("PT0S");
        video.appendChild(start);

        Element file = doc.createElement("File");
        file.setTextContent(filename);
        video.appendChild(file);

        Element type = doc.createElement("Type");
        type.setTextContent("Primary");
        video.appendChild(type);


        /*try (FileOutputStream output = new FileOutputStream("C:\\Users\\danie\\OneDrive\\Desktop" +
                "\\upload_manifest_generated.xml\\")) {
            writeXml(doc, output);
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }*/

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        return bos.toByteArray();
    }
}