package com.lostnfound.api.listing;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public ListingResponse createListing(
            CreateListingRequest request,
            Long authenticatedUserId
    ) {
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        Listing listing = new Listing(
                authenticatedUserId,
                request.title(),
                request.description(),
                request.category(),
                request.location()
        );

        Listing savedListing = listingRepository.save(listing);
        return ListingResponse.fromEntity(savedListing);
    }

    public List<ListingResponse> getLostListings() {
        return listingRepository
                .findByListingTypeOrderByCreatedAtDesc("LOST")
                .stream()
                .map(ListingResponse::fromEntity)
                .toList();
    }

    public ListingResponse getListingById(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"
                ));

        if (!"LOST".equals(listing.getListingType())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Listing not found"
            );
        }

        return ListingResponse.fromEntity(listing);
    }
}
