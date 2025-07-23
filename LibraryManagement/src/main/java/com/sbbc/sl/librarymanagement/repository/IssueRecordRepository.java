package com.sbbc.sl.librarymanagement.repository;

import com.sbbc.sl.librarymanagement.entity.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
}
