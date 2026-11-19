package com.lostnfound.api.listing;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemSearchController {

    private final ListingService listingService;

    public ItemSearchController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public List<ListingResponse> searchListings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type
    ) {
        return listingService.searchListings(keyword, type);
    }
}
