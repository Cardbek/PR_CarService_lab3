package com.autoservice.service;

import java.util.UUID;

public interface ServiceJobService {
    void markJobAsDone(UUID jobId);
}