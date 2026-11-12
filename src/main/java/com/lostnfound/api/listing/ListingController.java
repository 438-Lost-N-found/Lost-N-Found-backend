package com.lostnfound.api.listing;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
public class ListingController {

    private final ListingService listingService;
    private final AuthenticatedUserLookup userLookup;

    public ListingController(
            ListingService listingService,
            AuthenticatedUserLookup userLookup
    ) {
        this.listingService = listingService;
        this.userLookup = userLookup;
    }

    @GetMapping
    public List<ListingResponse> getLostListings() {
        return listingService.getLostListings();
    }

    @GetMapping("/{id}")
    public ListingResponse getListingById(@PathVariable Long id) {
        return listingService.getListingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse createListing(
            @Valid @RequestBody CreateListingRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        UUID supabaseUserId;

        try {
            supabaseUserId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid authenticated user ID"
            );
        }

        Long databaseUserId =
                userLookup.findDatabaseUserId(supabaseUserId);

        return listingService.createListing(
                request,
                databaseUserId
        );
    }
}
