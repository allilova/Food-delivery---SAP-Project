# BunnyBite Food Delivery Application

## Overview

This is a comprehensive food delivery application built with Spring Boot (backend) and Angular (frontend). The application allows customers to order food from restaurants, restaurant owners to manage their menus and orders, delivery drivers to track and fulfill deliveries, and administrators to oversee the entire platform, create restaurants, and manage users for system-wide operations.

## Features

### Customer Features
- User registration and authentication
- Browse restaurants by category
- Search for restaurants and food items
- View restaurant menus
- Add items to cart
- Place orders with delivery address
- Track order status
- Add restaurants to favorites
- View order history
- Rate and review orders

### Restaurant Owner Features
- Restaurant dashboard
- Process orders (accept, prepare, mark as ready)
- Order tracking and history

### Driver Features
- View and accept delivery orders
- Track deliveries in progress
- Mark orders as delivered
- Earn bonuses based on completed deliveries

### Admin Features
- Create and manage restaurants
- System-wide user management
- Access to all data and operations

## Technical Architecture

### Backend
- **Framework**: Spring Boot
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens)
- **Architecture**: REST API with MVC pattern

### Frontend
- **Framework**: Angular
- **UI Components**: Custom components with responsive design
- **State Management**: Service-based approach with Observables
- **Routing**: Angular Router with lazy loading

## Project Structure

### Backend Structure
- `config/`: Configuration classes (JWT, Security)
- `controller/`: REST API endpoints
- `dto/`: Data Transfer Objects
- `exception/`: Custom exceptions and handlers
- `model/`: Entity classes
- `repository/`: Data access interfaces
- `request/`: Request models
- `response/`: Response models
- `service/`: Business logic implementations

### Frontend Structure
- `components/`: Reusable UI components
- `core/`: Core functionality (navigation, footer)
- `guards/`: Route protection
- `interceptors/`: HTTP request/response handling
- `models/`: TypeScript interfaces
- `pages/`: Main application views
- `services/`: Data and state management
- `utils/`: Utility functions

## Setup Instructions

### Prerequisites
- Java 17+
- Node.js 18+
- npm 9+
- MySQL 8+
- Docker and Docker Compose (optional)

### Backend Setup
1. Clone the repository
2. Configure database connection in `application.properties`
3. Run `mvn clean install` to build the project
4. Start the application with `mvn spring-boot:run`

### Frontend Setup
1. Navigate to the frontend directory
2. Run `npm install` to install dependencies
3. Start the development server with `ng serve`
4. Access the application at `http://localhost:4200`

### Docker Setup
For containerized deployment, we provide Docker configuration:

1. Build Docker images:
    `bashdocker-compose build`

2. Start the application:
    `bashdocker-compose up -d`

3. Stop the application:
    `bashdocker-compose down`

## Testing

The application includes a comprehensive test suite to ensure reliability and stability:

### Backend Tests
- **Unit Tests**: Testing individual components in isolation
- **Integration Tests**: Testing interactions between components
- **Repository Tests**: Validating data access layer functionality
- **Service Tests**: Ensuring business logic works correctly
- **Controller Tests**: Testing API endpoints
- **Security Tests**: Verifying authentication and authorization

### Frontend Tests
- **Unit Tests**: Testing individual components and services
- **Component Tests**: Verifying UI component behavior
- **End-to-End Tests**: Testing complete user workflows

Tests are written using JUnit and Mockito for the backend, and Jasmine and Karma for the frontend. To run the tests:

```bash
# Backend tests
mvn test

# Frontend tests
cd frontend
ng test
```

We maintain a high test coverage to ensure application stability and facilitate future development.

## API Documentation

The API follows REST principles and uses standard HTTP methods and status codes.

### Authentication Endpoints
- `POST /auth/register`: Register a new user
- `POST /auth/login`: Authenticate and receive JWT token

### Restaurant Endpoints
- `GET /api/restaurants`: Get all restaurants
- `GET /api/restaurants/{id}`: Get restaurant by ID
- `GET /api/restaurants/search?keyword={keyword}`: Search restaurants
- `PUT /api/restaurants/{id}/add-favourites`: Toggle favorite status

### Food Endpoints
- `GET /api/food/menu/{menuId}`: Get food items by menu
- `GET /api/food/search?foodName={keyword}`: Search food items

### Order Endpoints
- `POST /api/orders`: Create a new order
- `GET /api/orders/user`: Get user's orders
- `GET /api/orders/{orderId}`: Get order by ID
- `PUT /api/orders/{orderId}/status?status={status}`: Update order status

### Cart Endpoints
- `GET /api/cart`: Get user's cart
- `POST /api/cart/items`: Add item to cart
- `PUT /api/cart/items/{itemId}`: Update cart item
- `DELETE /api/cart/items/{itemId}`: Remove item from cart
- `DELETE /api/cart`: Clear cart

## User Roles

The application defines the following user roles:
- `ROLE_CUSTOMER`: Standard user who can place orders
- `ROLE_RESTAURANT`: Restaurant owners/managers
- `ROLE_DRIVER`: Delivery personnel
- `ROLE_ADMIN`: System administrators

## Database Schema

The system uses the following core entities:
- `User`: User account information and authentication
- `Restaurant`: Restaurant details and configuration
- `Menu`: Restaurant menus with categories
- `Food`: Food items with details and availability
- `Cart` and `CartItem`: Shopping cart functionality
- `Order` and `OrderItem`: Order management
- `Review`: Ratings and reviews for restaurants and food
- `Payment`: Payment processing and tracking

## Future Enhancements

Planned features for future releases:
- Real-time order updates with WebSockets
- Advanced search with filters
- Payment gateway integration
- Loyalty program and discounts
- Mobile applications
- Machine learning for recommendations
- Analytics dashboard for restaurants
- Multi-language support

## Contributors

- Ahsen Arif: Backend, Database
- Aleksandra Lilova: Frontend, Backend
- Tuche Musa: Design, Documentation, Backend
- Sezer Dikme: Testing

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

Special thanks to all contributors and the open-source community.