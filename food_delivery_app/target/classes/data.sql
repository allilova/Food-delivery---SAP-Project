-- USERS (customer, restaurant, admin, driver)
INSERT INTO users ( name, email, password, phone_number, address, role)
VALUES
    ( 'Maria Ivanova', 'maria@example.com', 'pass123', '0888123456', 'Sofia, Bulgaria', 'ROLE_CUSTOMER'),
    ( 'Sushi Owner', 'sushi@example.com', 'owner123', '0888999111', 'Plovdiv, Bulgaria', 'ROLE_RESTAURANT'),
    ( 'Admin Adminov', 'admin@example.com', 'admin123', '0888111222', 'Varna, Bulgaria', 'ROLE_ADMIN'),
    ('Driver Driver', 'driver@example.com', 'driver123', '0888111333','Sofia, Bulgaria', 'ROLE_DRIVER');

INSERT INTO restaurant (
    restaurant_id,
    restaurant_user_id,
    restaurant_name,
    type,
    restaurant_address_id,
    contact_info_email,
    contact_info_phone,
    contact_info_social_media,
    opening_hours,
    closing_hours,
    open
)
VALUES (
    1,
    2,
    'Sakura Garden',
    'Asian',
    1,
    'contact@sakuragarden.bg',
    '0888999111',
    '@sakuragardenbg',
    '10:00',
    '22:00',
    true
);
