-- Display username captured at registration. Added with a temporary default so
-- any existing rows satisfy NOT NULL, then the default is dropped so new inserts
-- must supply it explicitly (the application always does).
ALTER TABLE app_user ADD COLUMN username VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE app_user ALTER COLUMN username DROP DEFAULT;
