package com.lostnfound.api.listing;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

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
    public List<ListingResponse> searchListings(String keyword, String type) {

        String normalizedKeyword =
                keyword == null || keyword.isBlank()
                        ? null
                        : keyword.trim();

        String normalizedType =
                type == null || type.isBlank()
                        ? null
                        : type.trim().toUpperCase(Locale.ROOT);

        if (normalizedType != null
                && !normalizedType.equals("LOST")
                && !normalizedType.equals("FOUND")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Type must be LOST or FOUND"
            );
        }

        return listingRepository
                .searchListings(normalizedKeyword, normalizedType)
                .stream()
                .map(ListingResponse::fromEntity)
                .toList();
    }
}
