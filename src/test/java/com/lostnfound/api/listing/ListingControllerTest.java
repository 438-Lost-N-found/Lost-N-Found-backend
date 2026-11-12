package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListingControllerTest {

    private ListingService listingService;
    private AuthenticatedUserLookup userLookup;
    private ListingController listingController;

    @BeforeEach
    void setUp() {
        listingService = mock(ListingService.class);
        userLookup = mock(AuthenticatedUserLookup.class);

        listingController = new ListingController(
                listingService,
                userLookup
        );
    }

    @Test
    void getLostListingsReturnsListings() {
        ListingResponse listing = new ListingResponse(
                10L, 1L,
                "Lost Backpack",
                "Black backpack",
                "Bags",
                "Library",
                "LOST",
                "OPEN",
                null
        );

        when(listingService.getLostListings())
                .thenReturn(List.of(listing));

        List<ListingResponse> result =
                listingController.getLostListings();

        assertEquals(1, result.size());
        assertEquals("Lost Backpack", result.get(0).title());
        assertEquals("LOST", result.get(0).listingType());

        verify(listingService).getLostListings();
    }

    @Test
    void getLostListingsReturnsEmptyList() {
        when(listingService.getLostListings())
                .thenReturn(List.of());

        List<ListingResponse> result =
                listingController.getLostListings();

        assertTrue(result.isEmpty());
        verify(listingService).getLostListings();
    }

    @Test
    void getListingByIdReturnsListing() {
        ListingResponse listing = new ListingResponse(
                10L, 1L,
                "Lost Phone",
                "Black iPhone",
                "Electronics",
                "Library",
                "LOST",
                "OPEN",
                null
        );

        when(listingService.getListingById(10L))
                .thenReturn(listing);

        ListingResponse result =
                listingController.getListingById(10L);

        assertEquals(10L, result.id());
        assertEquals("Lost Phone", result.title());
        assertEquals("Electronics", result.category());

        verify(listingService).getListingById(10L);
    }

    @Test
    void getListingByIdThrowsNotFoundForMissingListing() {
        when(listingService.getListingById(99L))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"
                ));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> listingController.getListingById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND,
                exception.getStatusCode());

        verify(listingService).getListingById(99L);
    }

    @Test
    void createListingWithAuthenticatedUser() {
        UUID supabaseUserId = UUID.randomUUID();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject())
                .thenReturn(supabaseUserId.toString());

        CreateListingRequest request = new CreateListingRequest(
                "Lost Backpack",
                "Black backpack",
                "Bags",
                "Library"
        );

        ListingResponse expected = new ListingResponse(
                10L, 1L,
                "Lost Backpack",
                "Black backpack",
                "Bags",
                "Library",
                "LOST",
                "OPEN",
                null
        );

        when(userLookup.findDatabaseUserId(supabaseUserId))
                .thenReturn(1L);

        when(listingService.createListing(request, 1L))
                .thenReturn(expected);

        ListingResponse result =
                listingController.createListing(request, jwt);

        assertEquals(10L, result.id());
        assertEquals(1L, result.userId());
        assertEquals("Lost Backpack", result.title());

        verify(userLookup)
                .findDatabaseUserId(supabaseUserId);

        verify(listingService)
                .createListing(request, 1L);
    }

    @Test
    void createListingWithoutAuthenticationReturnsUnauthorized() {
        CreateListingRequest request = new CreateListingRequest(
                "Lost Backpack",
                "Black backpack",
                "Bags",
                "Library"
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> listingController.createListing(request, null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED,
                exception.getStatusCode());

        verifyNoInteractions(userLookup, listingService);
    }

    @Test
    void createListingWithInvalidSupabaseIdReturnsUnauthorized() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("not-a-uuid");

        CreateListingRequest request = new CreateListingRequest(
                "Lost Backpack",
                "Black backpack",
                "Bags",
                "Library"
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> listingController.createListing(request, jwt)
        );

        assertEquals(HttpStatus.UNAUTHORIZED,
                exception.getStatusCode());

        verifyNoInteractions(userLookup, listingService);
    }
}
