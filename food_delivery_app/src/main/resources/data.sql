-- USERS (customer, restaurant, admin, driver)
INSERT INTO users ( name, email, password, phone_number, address, role)
VALUES
    ( 'Maria Ivanova', 'maria@example.com', 'pass123', '0888123456', 'Sofia, Bulgaria', 'ROLE_CUSTOMER'),
    ( 'Sushi Owner', 'sushi@example.com', 'owner123', '0888999111', 'Plovdiv, Bulgaria', 'ROLE_RESTAURANT'),
    ( 'Admin Adminov', 'admin@example.com', 'admin123', '0888111222', 'Varna, Bulgaria', 'ROLE_ADMIN'),
    ('Driver Driver', 'driver@example.com', 'driver123', '0888111333','Sofia, Bulgaria', 'ROLE_DRIVER');

