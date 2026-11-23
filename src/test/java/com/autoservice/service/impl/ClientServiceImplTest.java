package com.autoservice.service.impl;

import com.autoservice.entity.Client;
import com.autoservice.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock private ClientRepository clientRepository;
    @InjectMocks private ClientServiceImpl clientService;

    @Test
    void saveClient_ValidClient_ReturnsSavedClient() {
        Client client = new Client("Petro", "+380991234567");
        when(clientRepository.save(client)).thenReturn(client);

        Client saved = clientService.saveClient(client);

        assertEquals("Petro", saved.getName());
        verify(clientRepository).save(client);
    }

    @Test
    void getClientById_ExistingId_ReturnsClient() {
        UUID id = UUID.randomUUID();
        Client client = new Client("Petro", "+380991234567");
        when(clientRepository.findById(id)).thenReturn(Optional.of(client));

        Client found = clientService.getClientById(id);

        assertEquals("Petro", found.getName());
    }

    @Test
    void getClientById_NonExistingId_ThrowsIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> clientService.getClientById(id));
    }
}