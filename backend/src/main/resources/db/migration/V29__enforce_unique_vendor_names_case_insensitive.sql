
-- V29: Prevent vendor names that differ only by capitalization.
-- Example: "ABC Distributing" and "abc distributing".

CREATE UNIQUE INDEX ux_purchasing_vendors_name_lower
ON purchasing_vendors (LOWER(name));
