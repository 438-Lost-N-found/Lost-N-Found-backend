package com.lostnfound.api.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByListingTypeOrderByCreatedAtDesc(String listingType);

    @Query("""
        SELECT l FROM Listing l
        WHERE (:type IS NULL OR l.listingType = :type)
          AND (:keyword IS NULL
               OR LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY l.createdAt DESC
        """)
    List<Listing> searchListings(
            @Param("keyword") String keyword,
            @Param("type") String type
    );
}
