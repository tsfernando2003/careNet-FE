package com.CareNet.CareNet.repository;

import com.CareNet.CareNet.model.Document;
import com.CareNet.CareNet.model.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Document d WHERE d.caregiver = :caregiver")
    void deleteByCaregiver(@Param("caregiver") Caregiver caregiver);
}
