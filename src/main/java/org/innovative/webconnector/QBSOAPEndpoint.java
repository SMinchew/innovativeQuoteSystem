package org.innovative.webconnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

@Endpoint
public class QBSOAPEndpoint {

    private static final String NAMESPACE = "http://developer.intuit.com/";

    @Autowired
    private QBXMLProcessor qbxmlProcessor;

    @PayloadRoot(namespace = NAMESPACE, localPart = "authenticate")
    @ResponsePayload
    public Element authenticate(@RequestPayload Element request) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "authenticateResponse");
        Element result = doc.createElementNS(NAMESPACE, "authenticateResult");

        // QBWC expects a string array for authenticateResult
        Element ticket = doc.createElementNS(NAMESPACE, "string");
        ticket.setTextContent("innovative-ticket-" + System.currentTimeMillis());

        Element companyFile = doc.createElementNS(NAMESPACE, "string");
        // Empty string means use the currently open company file
        companyFile.setTextContent("");

        result.appendChild(ticket);
        result.appendChild(companyFile);
        response.appendChild(result);

        qbxmlProcessor.resetQueryStep();
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "sendRequestXML")
    @ResponsePayload
    public Element sendRequestXML(@RequestPayload Element request) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();

        Element response = doc.createElementNS(NAMESPACE, "sendRequestXMLResponse");
        Element result = doc.createElementNS(NAMESPACE, "sendRequestXMLResult");

        String ticket = request.getElementsByTagName("ticket").item(0).getTextContent();
        String query = qbxmlProcessor.getNextQuery();

        // Crucial: trim() helps prevent hidden whitespace that triggers 0x80040400
        result.setTextContent(query != null ? query.trim() : "");

        response.appendChild(result);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "receiveResponseXML")
    @ResponsePayload
    public Element receiveResponseXML(@RequestPayload Element request) throws Exception {
        String responseXML = request.getElementsByTagName("response").item(0).getTextContent();

        if (responseXML != null && !responseXML.isEmpty()) {
            qbxmlProcessor.processResponse(responseXML);
        }

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "receiveResponseXMLResponse");
        Element result = doc.createElementNS(NAMESPACE, "receiveResponseXMLResult");

        // Fixed: Use hasMoreQueries() to return 50 (continue) or 100 (done/100%)
        boolean hasMore = qbxmlProcessor.hasMoreQueries();
        result.setTextContent(hasMore ? "50" : "100");

        response.appendChild(result);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "closeConnection")
    @ResponsePayload
    public Element closeConnection(@RequestPayload Element request) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "closeConnectionResponse");
        Element result = doc.createElementNS(NAMESPACE, "closeConnectionResult");
        result.setTextContent("OK");
        response.appendChild(result);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "connectionError")
    @ResponsePayload
    public Element connectionError(@RequestPayload Element request) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "connectionErrorResponse");
        Element result = doc.createElementNS(NAMESPACE, "connectionErrorResult");
        result.setTextContent("done");
        response.appendChild(result);
        return response;
    }
    @PayloadRoot(namespace = NAMESPACE, localPart = "serverVersion")
    @ResponsePayload
    public Element serverVersion(@RequestPayload Element request) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "serverVersionResponse");
        Element result = doc.createElementNS(NAMESPACE, "serverVersionResult");
        result.setTextContent("1.0");
        response.appendChild(result);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "clientVersion")
    @ResponsePayload
    public Element clientVersion(@RequestPayload Element request) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "clientVersionResponse");
        Element result = doc.createElementNS(NAMESPACE, "clientVersionResult");
        // Empty string = version accepted. Return "W:<msg>" for a warning, "E:<msg>" to reject.
        result.setTextContent("");
        response.appendChild(result);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getLastError")
    @ResponsePayload
    public Element getLastError(@RequestPayload Element request) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element response = doc.createElementNS(NAMESPACE, "getLastErrorResponse");
        Element result = doc.createElementNS(NAMESPACE, "getLastErrorResult");
        result.setTextContent("");
        response.appendChild(result);
        return response;
    }
}