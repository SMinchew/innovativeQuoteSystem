package org.innovative.webconnector;

import org.innovative.repository.AssemblyRepository;
import org.innovative.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webconnector")
public class QBWebConnectorController {

    @Autowired
    private AssemblyRepository assemblyRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private QBXMLProcessor qbxmlProcessor;

    // Web Connector calls this to get the next QBXML request
    @PostMapping("/query")
    public Map<String, String> getQuery(@RequestBody Map<String, String> request) {
        String ticket = request.get("ticket");
        String response = request.get("response");

        Map<String, String> result = new HashMap<>();

        if (response != null && !response.isEmpty()) {
            // Process the response from QB
            qbxmlProcessor.processResponse(response);
        }

        // Return next query
        String nextQuery = qbxmlProcessor.getNextQuery();
        result.put("query", nextQuery);
        result.put("ticket", ticket);
        return result;
    }

    // Web Connector calls this to authenticate
    @PostMapping("/authenticate")
    public String[] authenticate(@RequestBody Map<String, String> request) {
        String username = request.get("strUserName");
        String password = request.get("strPassword");

        // Simple auth check - use your app credentials
        if ("admin".equals(username) && "password123".equals(password)) {
            return new String[]{"valid-ticket-" + System.currentTimeMillis(), ""};
        }
        return new String[]{"", "nvu"}; // not valid user
    }
}