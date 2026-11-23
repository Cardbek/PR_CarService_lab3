package com.autoservice.service.impl;

import com.autoservice.entity.ServiceJob;
import com.autoservice.repository.ServiceJobRepository;
import com.autoservice.service.ServiceJobService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ServiceJobServiceImpl implements ServiceJobService {

    private final ServiceJobRepository serviceJobRepository;

    public ServiceJobServiceImpl(ServiceJobRepository serviceJobRepository) {
        this.serviceJobRepository = serviceJobRepository;
    }

    @Override
    @Transactional
    public void markJobAsDone(UUID jobId) {
        ServiceJob job = serviceJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Service Job not found."));

        job.setDone(true);
        serviceJobRepository.save(job);
    }
}