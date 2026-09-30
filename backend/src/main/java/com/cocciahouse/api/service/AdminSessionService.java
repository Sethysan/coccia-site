package com.cocciahouse.api.service;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@Service
public class AdminSessionService {

    private final FindByIndexNameSessionRepository<? extends Session>
            sessionRepository;

    public AdminSessionService(
            FindByIndexNameSessionRepository<? extends Session>
                    sessionRepository
    ) {
        this.sessionRepository = sessionRepository;
    }

    public void revokeSessions(String username) {
        sessionRepository
                .findByPrincipalName(username)
                .keySet()
                .forEach(sessionRepository::deleteById);
    }
}
