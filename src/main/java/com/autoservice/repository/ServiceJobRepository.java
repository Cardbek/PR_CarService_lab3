package com.autoservice.repository;

import com.autoservice.entity.ServiceJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceJobRepository extends JpaRepository<ServiceJob, UUID> {
}