package com.lynknow.api.repository.view;

import com.lynknow.api.model.view.ViewTotalPendingCardVerification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ViewTotalPendingCardVerificationRepository extends JpaRepository<ViewTotalPendingCardVerification, Integer> {

    @Query("SELECT view FROM ViewTotalPendingCardVerification view")
    Page<ViewTotalPendingCardVerification> getData(Pageable pageable);

}
