package org.innovative.webconnector;

import org.innovative.model.*;
import org.innovative.repository.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QBXMLProcessor {

    private final AssemblyRepository assemblyRepository;
    private final CustomerRepository customerRepository;
    private final QuoteRepository quoteRepository;

    private final AtomicInteger queryStep = new AtomicInteger(0);
    private UUID currentProcessingQuoteId = null;

    public QBXMLProcessor(AssemblyRepository assemblyRepository,
                          CustomerRepository customerRepository,
                          QuoteRepository quoteRepository) {
        this.assemblyRepository = assemblyRepository;
        this.customerRepository = customerRepository;
        this.quoteRepository = quoteRepository;
    }

    public String getNextQuery() {
        int step = queryStep.getAndIncrement();
        String query;

        switch (step) {
            case 0:
                query = buildItemAssemblyQuery();
                break;
            case 1:
                query = buildCustomerQuery();
                break;
            case 2:
                // Check for quotes pending sync
                Quote pending = quoteRepository.findFirstByStatus(QuoteStatus.PENDING_QB);
                if (pending != null) {
                    currentProcessingQuoteId = pending.getId();
                    query = buildEstimateAddRequest(pending);
                } else {
                    query = "";
                }
                break;
            default:
                queryStep.set(0);
                return "";
        }
        return (query != null) ? query.replaceAll("[^\\x20-\\x7e]", "").trim() : "";
    }

    public void processResponse(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            // 1. Assemblies
            NodeList assemblyItems = doc.getElementsByTagName("ItemRet");
            System.out.println("Found assembly items: " + assemblyItems.getLength());
            for (int i = 0; i < assemblyItems.getLength(); i++) {
                parseAndSaveAssembly((Element) assemblyItems.item(i));
            }

            // 2. Customers
            NodeList customers = doc.getElementsByTagName("CustomerRet");
            for (int i = 0; i < customers.getLength(); i++) {
                parseAndSaveCustomer((Element) customers.item(i));
            }

            // 3. Confirm Sync
            NodeList estimateRs = doc.getElementsByTagName("EstimateAddRs");
            if (estimateRs.getLength() > 0) {
                handleEstimateResponse((Element) estimateRs.item(0));
            }

        } catch (Exception e) {
            System.err.println("QB Sync Error: " + e.getMessage());
        }
    }

    private void handleEstimateResponse(Element responseElement) {
        String statusCode = responseElement.getAttribute("statusCode");
        if ("0".equals(statusCode) && currentProcessingQuoteId != null) {
            quoteRepository.findById(currentProcessingQuoteId).ifPresent(quote -> {
                quote.setStatus(QuoteStatus.IN_QUICKBOOKS);
                quoteRepository.save(quote);
            });
        }
        currentProcessingQuoteId = null;
    }


    public String buildEstimateAddRequest(Quote quote) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xml.append("<?qbxml version=\"16.0\"?>");
        xml.append("<QBXML><QBXMLMsgsRq onError=\"stopOnError\">");
        xml.append("<EstimateAddRq requestID=\"101\">");
        xml.append("<EstimateAdd>");

        if (quote.getCustomer() != null && quote.getCustomer().getQbListId() != null) {
            xml.append("<CustomerRef><ListID>")
                    .append(quote.getCustomer().getQbListId())
                    .append("</ListID></CustomerRef>");
        }

        if (quote.getLines() != null) {
            for (QuoteLine line : quote.getLines()) {
                if (line.getAssembly() != null && line.getAssembly().getQbListId() != null) {
                    xml.append("<EstimateLineAdd>");
                    xml.append("<ItemRef><ListID>")
                            .append(line.getAssembly().getQbListId())
                            .append("</ListID></ItemRef>");
                    xml.append("<Quantity>").append(line.getQuantity()).append("</Quantity>");
                    xml.append("<Rate>").append(line.getUnitPrice()).append("</Rate>");
                    xml.append("</EstimateLineAdd>");
                }
            }
        }

        xml.append("</EstimateAdd></EstimateAddRq></QBXMLMsgsRq></QBXML>");
        return xml.toString().replaceAll("[^\\x20-\\x7e]", "").trim();
    }

    private String buildItemAssemblyQuery() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<?qbxml version=\"16.0\"?>" +
                "<QBXML>" +
                "<QBXMLMsgsRq onError=\"stopOnError\">" +
                "<ItemQueryRq requestID=\"1\" metaData=\"NoMetaData\">" +
                "<ItemTypeFilter>" +
                "<ItemTypeList>ItemAssembly</ItemTypeList>" +
                "</ItemTypeFilter>" +
                "</ItemQueryRq>" +
                "</QBXMLMsgsRq>" +
                "</QBXML>";
        return xml.replaceAll("[^\\x20-\\x7e]", "").trim();
    }

    private String buildCustomerQuery() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<?qbxml version=\"16.0\"?>" +
                "<QBXML>" +
                "<QBXMLMsgsRq onError=\"stopOnError\">" +
                "<CustomerQueryRq requestID=\"2\" metaData=\"NoMetaData\">" +
                "</CustomerQueryRq>" +
                "</QBXMLMsgsRq>" +
                "</QBXML>";
        return xml.replaceAll("[^\\x20-\\x7e]", "").trim();
    }

    private void parseAndSaveAssembly(Element item) {
        try {
            String listId = getTagValue(item, "ListID");
            Assembly assembly = assemblyRepository.findByQbListId(listId);
            if (assembly == null) {
                assembly = new Assembly();
                assembly.setQbListId(listId);
            }
            assembly.setName(getTagValue(item, "FullName"));
            String price = getTagValue(item, "SalesPrice");
            assembly.setDefaultPrice(price != null ? new BigDecimal(price) : BigDecimal.ZERO);
            assemblyRepository.save(assembly);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void parseAndSaveCustomer(Element item) {
        try {
            String listId = getTagValue(item, "ListID");
            Customer customer = customerRepository.findByQbListId(listId);
            if (customer == null) {
                customer = new Customer();
                customer.setQbListId(listId);
            }
            customer.setName(getTagValue(item, "FullName"));
            customerRepository.save(customer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String getTagValue(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        return (nodes.getLength() > 0) ? nodes.item(0).getTextContent() : null;
    }

    public boolean hasMoreQueries() {
        return queryStep.get() < 3;
    }



    public void resetQueryStep() {
        queryStep.set(0);
        currentProcessingQuoteId = null;
    }
}