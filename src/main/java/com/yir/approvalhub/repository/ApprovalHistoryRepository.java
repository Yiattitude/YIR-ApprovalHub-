package com.yir.approvalhub.repository;

import com.yir.approvalhub.entity.ApprovalHistory;
import com.yir.approvalhub.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByApplicationOrderByCreatedAtAsc(Application application);
}
