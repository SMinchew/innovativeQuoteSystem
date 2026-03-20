package org.innovative.services;

import jakarta.transaction.Transactional;
import org.innovative.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QBSyncService {

    @Autowired
    private QuoteRepository quoteRepository;

//    @Autowired
//    private QBConnector qbConnector;
//
//    @Autowired
//    private ItemRepository itemRepository;

    @Scheduled(fixedDelay = 900000)
    public void syncAssemblies() {
        // Will implement once QBFC SDK is added
        // IQBSDKLibrary sdk = QBSDKLibrary.getInstance();
        // IMsgSetRequest request = sdk.CreateMsgSetRequest("US", 16, 0);
        // IItemAssemblyQuery query = request.AppendItemAssemblyQueryRq();
        // IMsgSetResponse response = qbConnector.sendRequest(request);
        // parseAndSaveAssemblies(response);
    }

    @Scheduled(fixedDelay = 900000)
    public void syncCustomers() {
        // Will implement once QBFC SDK is added
    }

//    private void parseAndSaveAssemblies(Object response) {
//        // Placeholder - will parse QBXML response and upsert into PostgreSQL
//    }

//    @Scheduled(fixedDelay = 999999999)
//    @Transactional
//    public void cleanEmptyQuotes() {
//        quoteRepository.deleteEmptyDraftQuotes();
//    }
}