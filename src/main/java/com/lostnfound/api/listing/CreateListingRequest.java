package com.lostnfound.api.listing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateListingRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must be 150 characters or fewer")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @Size(max = 50, message = "Category must be 50 characters or fewer")
        String category,

        @Size(max = 255, message = "Location must be 255 characters or fewer")
        String location
) {
}
