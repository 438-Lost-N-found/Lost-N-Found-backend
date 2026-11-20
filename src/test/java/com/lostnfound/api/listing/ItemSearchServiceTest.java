package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemSearchServiceTest {

    private ListingRepository repository;
    private ListingService service;

    @BeforeEach
    void setUp() {
        repository = mock(ListingRepository.class);
        service = new ListingService(repository);
    }

    @Test
    void searchesByKeyword() {
        Listing listing = new Listing(
                1L, "Lost Phone", "Black iPhone",
                "Electronics", "Library"
        );

        when(repository.searchListings("phone", null))
                .thenReturn(List.of(listing));

        List<ListingResponse> results =
                service.searchListings(" phone ", null);

        assertEquals(1, results.size());
        assertEquals("Lost Phone", results.get(0).title());

        verify(repository).searchListings("phone", null);
    }

    @Test
    void filtersByLostType() {
        when(repository.searchListings(null, "LOST"))
                .thenReturn(List.of(
                        new Listing(1L, "Keys", "Silver keys",
                                "Keys", "Library")
                ));

        List<ListingResponse> results =
                service.searchListings(null, "lost");

        assertEquals(1, results.size());
        assertEquals("LOST", results.get(0).listingType());

        verify(repository).searchListings(null, "LOST");
    }

    @Test
    void combinesKeywordAndType() {
        when(repository.searchListings("phone", "LOST"))
                .thenReturn(List.of(
                        new Listing(1L, "Lost Phone", "Black phone",
                                "Electronics", "Library")
                ));

        List<ListingResponse> results =
                service.searchListings("phone", "LOST");

        assertEquals(1, results.size());
        verify(repository).searchListings("phone", "LOST");
    }

    @Test
    void returnsEmptyListWhenNoMatches() {
        when(repository.searchListings("missing", null))
                .thenReturn(List.of());

        List<ListingResponse> results =
                service.searchListings("missing", null);

        assertTrue(results.isEmpty());
    }

    @Test
    void rejectsInvalidType() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.searchListings(null, "INVALID")
        );

        assertEquals(HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verifyNoInteractions(repository);
    }

    @Test
    void blankFiltersReturnAllListings() {
        service.searchListings("  ", "  ");

        verify(repository).searchListings(null, null);
    }
}