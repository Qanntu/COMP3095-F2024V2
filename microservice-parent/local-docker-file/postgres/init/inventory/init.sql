
-- Create the inventory-service database and user
CREATE DATABASE "inventory-service";

-- Create user "admin" WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE "inventory-service" TO "admin";