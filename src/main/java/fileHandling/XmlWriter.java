package fileHandling;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlWriter {

    public void saveTransactionResult(String transactionId , String result) throws ParserConfigurationException, IOException,
            TransformerFactoryConfigurationError, TransformerException,
            SAXException {
        String xmlFilePath = "src\\response.xml";
        File xmlFile = new File(xmlFilePath);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory
                .newDocumentBuilder();
        Document document;
        Element rootElement;
        document = documentBuilder.newDocument();
        rootElement = document.createElement("Transactions");
        document.appendChild(rootElement);

        Element itemElement = document.createElement("transaction");
        rootElement.appendChild(itemElement);

        Element idElement = document.createElement("transactionId");
        Text id = document.createTextNode(transactionId);
        idElement.appendChild(id);
        itemElement.appendChild(idElement);

        Element pathElement = document.createElement("result");
        Text path = document.createTextNode(result);
        pathElement.appendChild(path);
        itemElement.appendChild(pathElement);

        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4");

        StreamResult streamResult = new StreamResult(new StringWriter());
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, streamResult);
        String xmlString = streamResult.getWriter().toString();

        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(xmlFile , true)));
        bufferedWriter.write(xmlString);
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}

