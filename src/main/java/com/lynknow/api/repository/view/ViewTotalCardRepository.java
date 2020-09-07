package com.lynknow.api.repository.view;

import com.lynknow.api.model.view.ViewTotalCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ViewTotalCardRepository extends JpaRepository<ViewTotalCard, Integer> {

    @Query("SELECT view FROM ViewTotalCard view")
    Page<ViewTotalCard> getData(Pageable pageable);

}
