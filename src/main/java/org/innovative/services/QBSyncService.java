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



    @Scheduled(fixedDelay = 900000)
    public void syncAssemblies() {

    }

    @Scheduled(fixedDelay = 900000)
    public void syncCustomers() {
    
    }


}