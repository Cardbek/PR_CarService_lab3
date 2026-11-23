package com.autoservice.service;

import com.autoservice.entity.Client;

import java.util.UUID;

public interface ClientService {
    Client saveClient(Client client);

    Client getClientById(UUID id);
}