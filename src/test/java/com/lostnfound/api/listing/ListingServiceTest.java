package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    private ListingService listingService;

    @BeforeEach
    void setUp() {
        listingService = new ListingService(listingRepository);
    }

    @Test
    void createsLostListingForAuthenticatedUser() {
        CreateListingRequest request = new CreateListingRequest(
                "Lost Backpack",
                "Black backpack",
                "Bags",
                "Library"
        );

        when(listingRepository.save(any(Listing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ListingResponse response = listingService.createListing(
                request, 1L
        );

        assertEquals(1L, response.userId());
        assertEquals("Lost Backpack", response.title());
        assertEquals("Black backpack", response.description());
        assertEquals("Bags", response.category());
        assertEquals("Library", response.location());
        assertEquals("LOST", response.listingType());
        assertEquals("OPEN", response.status());

        verify(listingRepository).save(any(Listing.class));
    }

    @Test
    void rejectsCreationWithoutAuthenticatedUser() {
        CreateListingRequest request = new CreateListingRequest(
                "Lost Backpack",
                "Black backpack",
                "Bags",
                "Library"
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> listingService.createListing(request, null)
        );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                exception.getStatusCode()
        );

        verifyNoInteractions(listingRepository);
    }

    @Test
    void returnsLostListings() {
        Listing listing = new Listing(
                1L,
                "Lost Keys",
                "Silver keychain",
                "Keys",
                "Library"
        );

        when(listingRepository
                .findByListingTypeOrderByCreatedAtDesc("LOST"))
                .thenReturn(List.of(listing));

        List<ListingResponse> results =
                listingService.getLostListings();

        assertEquals(1, results.size());
        assertEquals("Lost Keys", results.get(0).title());
        assertEquals("LOST", results.get(0).listingType());

        verify(listingRepository)
                .findByListingTypeOrderByCreatedAtDesc("LOST");
    }

    @Test
    void returnsNotFoundForMissingListing() {
        when(listingRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> listingService.getListingById(99L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
    }

    @Test
    void returnsListingById() {
        Listing listing = new Listing(
                1L,
                "Lost Phone",
                "Black iPhone",
                "Electronics",
                "Library"
        );

        when(listingRepository.findById(10L))
                .thenReturn(Optional.of(listing));

        ListingResponse response =
                listingService.getListingById(10L);

        assertEquals("Lost Phone", response.title());
        assertEquals("Black iPhone", response.description());
        assertEquals("Electronics", response.category());
        assertEquals("Library", response.location());
        assertEquals("LOST", response.listingType());

        verify(listingRepository).findById(10L);
    }

    @Test
    void rejectsFoundListingWhenRetrievingLostListing() {
        Listing listing = mock(Listing.class);

        when(listingRepository.findById(10L))
                .thenReturn(Optional.of(listing));

        when(listing.getListingType())
                .thenReturn("FOUND");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> listingService.getListingById(10L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(listingRepository).findById(10L);
    }
}
