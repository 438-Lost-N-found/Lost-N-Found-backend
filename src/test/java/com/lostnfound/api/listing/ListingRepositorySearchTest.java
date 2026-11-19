package com.lostnfound.api.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import(AuthenticatedUserLookupTestConfig.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:listing-search-test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
})
@Transactional
@Sql(statements = "ALTER TABLE public.listings ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP")
class ListingRepositorySearchTest {

    @Autowired
    private ListingRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        repository.save(new Listing(
                1L, "Lost Phone", "Black iPhone",
                "Electronics", "Library"
        ));

        repository.save(new Listing(
                1L, "Lost Backpack", "Blue school bag",
                "Bags", "Student Center"
        ));

        repository.save(new Listing(
                1L, "Missing Keys", "Silver keychain",
                "Keys", "Library"
        ));

        repository.flush();

        jdbcTemplate.update("""
                INSERT INTO public.listings
                    (user_id, title, description, category, location, listing_type, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                1L, "Found Water Bottle", "Green reusable bottle",
                "Accessories", "Library", "FOUND", "OPEN");
    }

    @Test
    void searchesByTitle() {
        List<Listing> results =
                repository.searchListings("phone", null);

        assertEquals(1, results.size());
        assertEquals("Lost Phone", results.get(0).getTitle());
    }

    @Test
    void searchesByDescription() {
        List<Listing> results =
                repository.searchListings("silver", null);

        assertEquals(1, results.size());
        assertEquals("Missing Keys", results.get(0).getTitle());
    }

    @Test
    void searchIsCaseInsensitive() {
        List<Listing> results =
                repository.searchListings("PHONE", null);

        assertEquals(1, results.size());
        assertEquals("Lost Phone", results.get(0).getTitle());
    }

    @Test
    void filtersLostListings() {
        List<Listing> results =
                repository.searchListings(null, "LOST");

        assertEquals(3, results.size());
        assertTrue(results.stream()
                .allMatch(l -> "LOST".equals(l.getListingType())));
    }

    @Test
    void filtersFoundListings() {
        List<Listing> results =
                repository.searchListings(null, "FOUND");

        assertEquals(1, results.size());
        assertEquals("Found Water Bottle", results.get(0).getTitle());
    }

    @Test
    void combinesKeywordAndType() {
        List<Listing> results =
                repository.searchListings("phone", "LOST");

        assertEquals(1, results.size());
        assertEquals("Lost Phone", results.get(0).getTitle());
    }

    @Test
    void returnsEmptyListWhenNoMatches() {
        List<Listing> results =
                repository.searchListings("nonexistent", null);

        assertTrue(results.isEmpty());
    }

    @Test
    void returnsAllListingsWithoutFilters() {
        List<Listing> results =
                repository.searchListings(null, null);

        assertEquals(4, results.size());
    }
}
