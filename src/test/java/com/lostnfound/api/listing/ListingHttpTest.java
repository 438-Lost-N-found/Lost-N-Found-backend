package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ListingHttpTest {

    private ListingService listingService;
    private AuthenticatedUserLookup userLookup;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        listingService = mock(ListingService.class);
        userLookup = mock(AuthenticatedUserLookup.class);

        ListingController controller =
                new ListingController(listingService, userLookup);

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .setCustomArgumentResolvers(
                        new AuthenticationPrincipalArgumentResolver()
                )
                .build();
    }

    @Test
    void getLostListingsReturnsOk() throws Exception {
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

        mockMvc.perform(get("/api/v1/items/lost"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title")
                        .value("Lost Backpack"))
                .andExpect(jsonPath("$[0].listingType")
                        .value("LOST"));

        verify(listingService).getLostListings();
    }

    @Test
    void getLostListingsReturnsEmptyArray() throws Exception {
        when(listingService.getLostListings())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/items/lost"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(listingService).getLostListings();
    }

    @Test
    void getListingByIdReturnsOk() throws Exception {
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

        mockMvc.perform(get("/api/v1/items/lost/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title")
                        .value("Lost Phone"))
                .andExpect(jsonPath("$.category")
                        .value("Electronics"));

        verify(listingService).getListingById(10L);
    }

    @Test
    void getListingByIdReturnsNotFound() throws Exception {
        when(listingService.getListingById(99L))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found"
                ));

        mockMvc.perform(get("/api/v1/items/lost/99"))
                .andExpect(status().isNotFound());

        verify(listingService).getListingById(99L);
    }

    @Test
    void createListingWithoutJwtReturnsUnauthorized()
            throws Exception {

        String request = """
                {
                  "title": "Lost Backpack",
                  "description": "Black backpack",
                  "category": "Bags",
                  "location": "Library"
                }
                """;

        mockMvc.perform(post("/api/v1/items/lost")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userLookup, listingService);
    }

    @Test
    void createListingWithMissingTitleReturnsBadRequest()
            throws Exception {

        String request = """
                {
                  "title": "",
                  "description": "Black backpack",
                  "category": "Bags",
                  "location": "Library"
                }
                """;

        mockMvc.perform(post("/api/v1/items/lost")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userLookup, listingService);
    }

    @Test
    void createListingWithMissingDescriptionReturnsBadRequest()
            throws Exception {

        String request = """
                {
                  "title": "Lost Backpack",
                  "description": "",
                  "category": "Bags",
                  "location": "Library"
                }
                """;

        mockMvc.perform(post("/api/v1/items/lost")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userLookup, listingService);
    }
}
