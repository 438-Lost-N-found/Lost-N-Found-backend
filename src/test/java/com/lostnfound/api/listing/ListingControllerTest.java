package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListingControllerTest {

    private ListingService listingService;
    private ListingController listingController;

    @BeforeEach
    void setUp() {
        listingService = mock(ListingService.class);
        listingController = new ListingController(listingService);
    }

    @Test
    void getLostListingsReturnsListings() {
        ListingResponse listing = new ListingResponse(
                10L,
                1L,
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
                10L,
                1L,
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

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(listingService).getListingById(99L);
    }
}
