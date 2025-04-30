-- Insert sample users
INSERT INTO users (name, email, password, phone_number, address, role) VALUES
('John Doe', 'john@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '1234567890', '123 Main St', 'ROLE_CUSTOMER'),
('Jane Smith', 'jane@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '0987654321', '456 Oak St', 'ROLE_CUSTOMER'),
('Admin User', 'admin@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '5555555555', '789 Admin St', 'ROLE_ADMIN');

-- Insert sample addresses for restaurants
INSERT INTO address (addressid, city, country, state, street) VALUES
(1, 'New York', 'USA', 'NY', '123 Pizza St'),
(2, 'Los Angeles', 'USA', 'CA', '456 Burger Ave'),
(3, 'Chicago', 'USA', 'IL', '789 Sushi Rd');

-- Insert sample restaurants
INSERT INTO restaurants (restaurant_name, type, opening_hours, closing_hours, open, average_rating, email, phone, social_media, restaurant_address_id) VALUES
('Pizza Palace', 'Italian', '10:00', '22:00', true, 0.0, 'pizza@example.com', '1112223333', 'pizzapalace', 1),
('Burger King', 'Fast Food', '11:00', '23:00', true, 0.0, 'burger@example.com', '4445556666', 'burgerking', 2),
('Sushi Master', 'Japanese', '12:00', '21:00', true, 0.0, 'sushi@example.com', '7778889999', 'sushimaster', 3);

-- Insert sample menus
INSERT INTO menu (restaurant_id, category_name) VALUES
(1, 'Pizza Menu'),
(2, 'Burger Menu'),
(3, 'Sushi Menu');

-- Insert sample categories
INSERT INTO category (category_name, menu_id) VALUES
('Pizza', 1),
('Burgers', 2),
('Sushi', 3);

-- Insert sample foods
INSERT INTO foods (name, description, price, image_url, is_available, restaurant_id, category_id, average_rating) VALUES
('Margherita Pizza', 'Classic tomato and mozzarella pizza', 12.99, 'pizza1.jpg', true, 1, 1, 0.0),
('Pepperoni Pizza', 'Pizza with pepperoni toppings', 14.99, 'pizza2.jpg', true, 1, 1, 0.0),
('Whopper', 'Classic beef burger', 8.99, 'burger1.jpg', true, 2, 2, 0.0),
('Chicken Royale', 'Grilled chicken burger', 7.99, 'burger2.jpg', true, 2, 2, 0.0),
('California Roll', 'Crab, avocado, and cucumber roll', 9.99, 'sushi1.jpg', true, 3, 3, 0.0),
('Salmon Nigiri', 'Fresh salmon over rice', 6.99, 'sushi2.jpg', true, 3, 3, 0.0);

-- Insert sample menu_food relationships
INSERT INTO menu_food (menu_id, food_id) VALUES
(1, 1), (1, 2),
(2, 3), (2, 4),
(3, 5), (3, 6);

-- Insert sample reviews
INSERT INTO reviews (user_id, restaurant_id, food_id, rating, comment, created_at, updated_at) VALUES
(1, 1, 1, 5, 'Best pizza ever!', NOW(), NOW()),
(2, 1, 2, 4, 'Great pepperoni pizza', NOW(), NOW()),
(1, 2, 3, 5, 'Amazing burger', NOW(), NOW()),
(2, 2, 4, 4, 'Good chicken burger', NOW(), NOW()),
(1, 3, 5, 5, 'Fresh and delicious sushi', NOW(), NOW()),
(2, 3, 6, 4, 'Nice salmon nigiri', NOW(), NOW());

