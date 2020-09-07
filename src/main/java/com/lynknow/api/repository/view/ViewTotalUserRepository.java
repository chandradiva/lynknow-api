package com.lynknow.api.repository.view;

import com.lynknow.api.model.view.ViewTotalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ViewTotalUserRepository extends JpaRepository<ViewTotalUser, Integer> {

    @Query("SELECT view FROM ViewTotalUser view")
    Page<ViewTotalUser> getData(Pageable pageable);

}
