package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

        ListingResponse response = listingService.createListing(request, 1L);

        assertEquals(1L, response.userId());
        assertEquals("Lost Backpack", response.title());
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

        assertThrows(ResponseStatusException.class,
                () -> listingService.createListing(request, null));

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

        when(listingRepository.findByListingTypeOrderByCreatedAtDesc("LOST"))
                .thenReturn(List.of(listing));

        List<ListingResponse> results = listingService.getLostListings();

        assertEquals(1, results.size());
        assertEquals("Lost Keys", results.get(0).title());
    }

    @Test
    void returnsNotFoundForMissingListing() {
        when(listingRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> listingService.getListingById(99L));
    }
}
