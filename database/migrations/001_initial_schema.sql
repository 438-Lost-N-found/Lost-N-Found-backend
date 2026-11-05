-- Lost-N-Found
-- Sprint 1: Initial Supabase PostgreSQL Schema

-- Users
CREATE TABLE IF NOT EXISTS public.users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    oauth_provider VARCHAR(30),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Lost and found item listings
CREATE TABLE IF NOT EXISTS public.listings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES public.users(id),
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50),
    location VARCHAR(255),
    listing_type VARCHAR(10) NOT NULL
        CHECK (listing_type IN ('LOST', 'FOUND')),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'RESOLVED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Claims for found items
CREATE TABLE IF NOT EXISTS public.claims (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    listing_id BIGINT NOT NULL
        REFERENCES public.listings(id),
    claimant_id BIGINT NOT NULL
        REFERENCES public.users(id),
    message TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_listings_user_id
    ON public.listings(user_id);

CREATE INDEX IF NOT EXISTS idx_listings_type
    ON public.listings(listing_type);

CREATE INDEX IF NOT EXISTS idx_claims_listing_id
    ON public.claims(listing_id);

CREATE INDEX IF NOT EXISTS idx_claims_claimant_id
    ON public.claims(claimant_id);

-- Enable Row Level Security
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.listings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.claims ENABLE ROW LEVEL SECURITY;
