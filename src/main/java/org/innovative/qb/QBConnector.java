package org.innovative.qb;

//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import org.springframework.stereotype.Service;
//
//@Service
//public class QBConnector {
//
//    private IQBSessionManager sessionManager;
//
//    @PostConstruct
//    public void init() {
//        sessionManager = new QBSessionManager();
//        sessionManager.setAppID("");
//        sessionManager.setAppLogin("QuotingApp");
//        sessionManager.setCompanyFileName(""); // empty = currently open file
//        sessionManager.openConnection("", "Assembly Quoting App");
//        sessionManager.beginSession("", ENOpenMode.omDontCare);
//    }
//
//    public IMsgSetResponse sendRequest(IMsgSetRequest request) {
//        return sessionManager.doRequests(request);
//    }
//
//    @PreDestroy
//    public void cleanup() {
//        sessionManager.endSession();
//        sessionManager.closeConnection();
//    }
//}