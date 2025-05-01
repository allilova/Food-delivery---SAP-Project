-- This script only inserts data if the tables are empty
-- Check if users table is empty before inserting admin user
INSERT INTO users (name, email, password, phone_number, address, role)
SELECT 'Admin User', 'admin@example.com', '$2a$10$yfIHMg4TibJKF2FQBpgj3.MKwz0uYrJjYzBwrSXZJiYLM3MQdmHnm', '1234567890', '123 Admin St', 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com');

-- Insert restaurant user only if not exists
INSERT INTO users (name, email, password, phone_number, address, role)
SELECT 'Restaurant Owner', 'restaurant@example.com', '$2a$10$yfIHMg4TibJKF2FQBpgj3.MKwz0uYrJjYzBwrSXZJiYLM3MQdmHnm', '9876543210', '456 Restaurant Ave', 'ROLE_RESTAURANT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'restaurant@example.com');

-- Insert customer user only if not exists
INSERT INTO users (name, email, password, phone_number, address, role)
SELECT 'Customer User', 'customer@example.com', '$2a$10$yfIHMg4TibJKF2FQBpgj3.MKwz0uYrJjYzBwrSXZJiYLM3MQdmHnm', '5555555555', '789 Customer Blvd', 'ROLE_CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer@example.com');