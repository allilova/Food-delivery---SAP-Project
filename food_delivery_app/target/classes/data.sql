-- USERS (customer, restaurant, admin, driver)
INSERT INTO users ( name, email, password, phone_number, address, role)
VALUES
    ( 'Maria Ivanova', 'maria@example.com', 'pass123', '0888123456', 'Sofia, Bulgaria', 'ROLE_CUSTOMER'),
    ( 'Sushi Owner', 'sushi@example.com', 'owner123', '0888999111', 'Plovdiv, Bulgaria', 'ROLE_RESTAURANT'),
    ( 'Admin Adminov', 'admin@example.com', 'admin123', '0888111222', 'Varna, Bulgaria', 'ROLE_ADMIN'),
    ('Driver Driver', 'driver@example.com', 'driver123', '0888111333','Sofia, Bulgaria', 'ROLE_DRIVER');

-- Address
INSERT INTO address (id)
VALUES (1), (2), (3);

-- Restaurants
INSERT INTO restaurant (restaurant_id, restaurant_name, type, restaurant_address_id, opening_hours, closing_hours, open, restaurant_id)
VALUES 
    (1, 'Sushi Paradise', 'Japanese', 1, '10:00', '22:00', true, 2),
    (2, 'Pizza Heaven', 'Italian', 2, '11:00', '23:00', true, 3),
    (3, 'Burger Joint', 'American', 3, '10:00', '23:30', true, 4);

-- Images for restaurants
INSERT INTO restaurant_images (restaurant_restaurant_id, images)
VALUES 
    (1, 'sushi.jpg'),
    (2, 'pizza.jpg'),
    (3, 'burger.jpg');
