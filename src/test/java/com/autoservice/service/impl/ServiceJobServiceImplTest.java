package com.autoservice.service.impl;

import com.autoservice.entity.ServiceJob;
import com.autoservice.repository.ServiceJobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceJobServiceImplTest {

    @Mock private ServiceJobRepository repository;
    @InjectMocks private ServiceJobServiceImpl service;

    @Test
    void markJobAsDone_ExistingJob_SetsDoneToTrueAndSaves() {
        ServiceJob job = new ServiceJob();
        job.setDone(false);
        when(repository.findById(any())).thenReturn(Optional.of(job));

        service.markJobAsDone(UUID.randomUUID());

        assertTrue(job.isDone());
        verify(repository).save(job);
    }
}