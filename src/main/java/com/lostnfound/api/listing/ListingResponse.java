package com.lostnfound.api.listing;

import java.time.OffsetDateTime;

public record ListingResponse(
        Long id,
        Long userId,
        String title,
        String description,
        String category,
        String location,
        String listingType,
        String status,
        OffsetDateTime createdAt
) {

    public static ListingResponse fromEntity(Listing listing) {
        return new ListingResponse(
                listing.getId(),
                listing.getUserId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getCategory(),
                listing.getLocation(),
                listing.getListingType(),
                listing.getStatus(),
                listing.getCreatedAt()
        );
    }
}
