package com.yir.approvalhub.repository;

import com.yir.approvalhub.entity.ReimbursementApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimbursementApplicationRepository extends JpaRepository<ReimbursementApplication, Long> {
}
