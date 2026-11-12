package com.lostnfound.api.listing;

import java.util.UUID;

public interface AuthenticatedUserLookup {

    Long findDatabaseUserId(UUID supabaseUserId);
}
