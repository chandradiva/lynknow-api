package com.lynknow.api.repository.view;

import com.lynknow.api.model.view.ViewTotalPendingPersonalVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ViewTotalPendingPersonalVerificationRepository extends JpaRepository<ViewTotalPendingPersonalVerification, Integer> {

    @Query("SELECT view FROM ViewTotalPendingPersonalVerification view")
    Page<ViewTotalPendingPersonalVerification> getData(Pageable pageable);

}
