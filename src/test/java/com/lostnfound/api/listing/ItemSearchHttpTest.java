package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemSearchHttpTest {

    private ListingService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(ListingService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(
                new ItemSearchController(service)
        ).build();
    }

    @Test
    void searchReturnsMatchingListings() throws Exception {
        ListingResponse listing = new ListingResponse(
                10L, 1L, "Lost Phone", "Black iPhone",
                "Electronics", "Library",
                "LOST", "OPEN", null
        );

        when(service.searchListings("phone", "LOST"))
                .thenReturn(List.of(listing));

        mockMvc.perform(get("/api/v1/items")
                        .param("keyword", "phone")
                        .param("type", "LOST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Lost Phone"))
                .andExpect(jsonPath("$[0].listingType")
                        .value("LOST"));

        verify(service).searchListings("phone", "LOST");
    }

    @Test
    void searchReturnsEmptyArray() throws Exception {
        when(service.searchListings("missing", null))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/items")
                        .param("keyword", "missing"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void searchRejectsInvalidType() throws Exception {
        when(service.searchListings(null, "INVALID"))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Type must be LOST or FOUND"
                ));

        mockMvc.perform(get("/api/v1/items")
                        .param("type", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchWithoutFiltersReturnsOk() throws Exception {
        when(service.searchListings(null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service).searchListings(null, null);
    }
}