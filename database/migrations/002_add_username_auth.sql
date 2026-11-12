
-- Add username and Supabase Auth account reference
ALTER TABLE public.users
ADD COLUMN IF NOT EXISTS username VARCHAR(50);

ALTER TABLE public.users
ADD COLUMN IF NOT EXISTS auth_user_id UUID;

-- Usernames must be unique (case-insensitive)
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username
ON public.users (LOWER(username))
WHERE username IS NOT NULL;

-- Each Supabase Auth account can only be linked once
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_auth_user_id
ON public.users (auth_user_id)
WHERE auth_user_id IS NOT NULL;
