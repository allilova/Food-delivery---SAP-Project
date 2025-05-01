package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.exception.ResourceNotFoundException;
import com.example.food_delivery_app.model.Address;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.AddressRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.repository.UserRepository;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    
    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public RestaurantServiceImpl(
            RestaurantRepository restaurantRepository,
            AddressRepository addressRepository,
            UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<RestaurantResponseDto> getAllRestaurantsDto() {
        logger.debug("Getting all restaurants as DTOs");
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponseDto> searchRestaurantsDto(String keyword) {
        logger.debug("Searching restaurants with keyword: {}", keyword);
        List<Restaurant> restaurants = restaurantRepository.findBySearchQuery(keyword);
        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {
        logger.debug("Creating new restaurant for user: {}", user.getEmail());
        
        // Save the address first
        Address address = addressRepository.save(req.getRestaurantAddress());
        
        // Create and populate the restaurant entity
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantAddress(address);
        restaurant.setContactInfo(req.getContactInfo());
        restaurant.setType(req.getType());
        restaurant.setClosingHours(req.getClosingHours());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setImages(req.getImages());
        restaurant.setRestaurantName(req.getRestaurantName());
        restaurant.setRestaurant(user);
        restaurant.setOpen(true); // Default to open
        
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatedRestaurant) throws Exception {
        logger.debug("Updating restaurant with ID: {}", restaurantId);
        
        Restaurant restaurant = findById(restaurantId);
        
        // Update fields if provided in the request
        if (updatedRestaurant.getRestaurantName() != null) {
            restaurant.setRestaurantName(updatedRestaurant.getRestaurantName());
        }
        
        if (updatedRestaurant.getType() != null) {
            restaurant.setType(updatedRestaurant.getType());
        }
        
        if (updatedRestaurant.getContactInfo() != null) {
            restaurant.setContactInfo(updatedRestaurant.getContactInfo());
        }
        
        if (updatedRestaurant.getOpeningHours() != null) {
            restaurant.setOpeningHours(updatedRestaurant.getOpeningHours());
        }
        
        if (updatedRestaurant.getClosingHours() != null) {
            restaurant.setClosingHours(updatedRestaurant.getClosingHours());
        }
        
        if (updatedRestaurant.getImages() != null && !updatedRestaurant.getImages().isEmpty()) {
            restaurant.setImages(updatedRestaurant.getImages());
        }
        
        // Update address if provided
        if (updatedRestaurant.getRestaurantAddress() != null) {
            Address updatedAddress = updatedRestaurant.getRestaurantAddress();
            
            // If there's an existing address, update its properties
            if (restaurant.getRestaurantAddress() != null) {
                Address existingAddress = restaurant.getRestaurantAddress();
                
                if (updatedAddress.getStreet() != null) {
                    existingAddress.setStreet(updatedAddress.getStreet());
                }
                
                if (updatedAddress.getCity() != null) {
                    existingAddress.setCity(updatedAddress.getCity());
                }
                
                addressRepository.save(existingAddress);
            } else {
                // Otherwise create a new address
                Address newAddress = addressRepository.save(updatedAddress);
                restaurant.setRestaurantAddress(newAddress);
            }
        }
        
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void deleteRestaurant(Long restaurantId) throws Exception {
        logger.debug("Deleting restaurant with ID: {}", restaurantId);
        
        Restaurant restaurant = findById(restaurantId);
        restaurantRepository.delete(restaurant);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        logger.debug("Getting all restaurants");
        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> searchRestaurants(String keyword) {
        logger.debug("Searching restaurants with keyword: {}", keyword);
        return restaurantRepository.findBySearchQuery(keyword);
    }

    @Override
    public Restaurant findById(Long id) throws Exception {
        logger.debug("Finding restaurant by ID: {}", id);
        
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", id));
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long restaurantId, User user) throws Exception {
        logger.debug("Toggling favorite status for restaurant ID: {} by user: {}", restaurantId, user.getEmail());
        
        Restaurant restaurant = findById(restaurantId);
        
        // Create DTO for the restaurant
        RestaurantDto dto = new RestaurantDto();
        dto.setId(restaurant.getId());
        dto.setRestaurantName(restaurant.getRestaurantName());
        
        if (restaurant.getImages() != null && !restaurant.getImages().isEmpty()) {
            dto.setImages(restaurant.getImages());
        }
        
        if (restaurant.getRestaurantAddress() != null) {
            dto.setAddress(restaurant.getRestaurantAddress().getStreet() + ", " + 
                           restaurant.getRestaurantAddress().getCity());
        }
        
        if (restaurant.getContactInfo() != null) {
            dto.setEmail(restaurant.getContactInfo().getEmail());
        }
        
        // Check if restaurant is already in favorites
        boolean alreadyFavorite = user.getFavourites().stream()
                .anyMatch(fav -> fav.getId() != null && fav.getId().equals(dto.getId()));
        
        // Toggle favorite status
        if (alreadyFavorite) {
            user.getFavourites().removeIf(fav -> fav.getId() != null && fav.getId().equals(dto.getId()));
        } else {
            user.getFavourites().add(dto);
        }
        
        userRepository.save(user);
        return !alreadyFavorite; // Return true if added to favorites, false if removed
    }

    @Override
    @Transactional
    public Restaurant updateRestaurantStatus(Long restaurantId) throws Exception {
        logger.debug("Updating status for restaurant ID: {}", restaurantId);
        
        Restaurant restaurant = findById(restaurantId);
        restaurant.setOpen(!restaurant.isOpen());
        
        return restaurantRepository.save(restaurant);
    }

    @Override
    public RestaurantResponseDto convertToDto(Restaurant restaurant) {
        RestaurantResponseDto dto = new RestaurantResponseDto();
        
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getRestaurantName());
        
        // Set image URL (first image from the list or default)
        dto.setImgUrl(
                restaurant.getImages() != null && !restaurant.getImages().isEmpty()
                        ? restaurant.getImages().get(0)
                        : "default-restaurant.jpg"
        );
        
        // Set address
        if (restaurant.getRestaurantAddress() != null) {
            StringBuilder addressBuilder = new StringBuilder();
            
            if (restaurant.getRestaurantAddress().getStreet() != null) {
                addressBuilder.append(restaurant.getRestaurantAddress().getStreet());
            }
            
            if (restaurant.getRestaurantAddress().getCity() != null) {
                if (addressBuilder.length() > 0) {
                    addressBuilder.append(", ");
                }
                addressBuilder.append(restaurant.getRestaurantAddress().getCity());
            }
            
            dto.setAddress(addressBuilder.toString());
        } else {
            dto.setAddress("");
        }
        
        // Calculate average rating from ratings or use default
        if (restaurant.getRatings() != null && !restaurant.getRatings().isEmpty()) {
            double avgRating = restaurant.getRatings().stream()
                    .mapToInt(rating -> rating.getRating())
                    .average()
                    .orElse(4.5);
            dto.setRating(avgRating);
        } else {
            dto.setRating(4.5); // Default rating
        }
        
        dto.setFoodType(restaurant.getType());
        
        // Set delivery time (could be calculated based on some logic in the future)
        dto.setTimeDelivery(restaurant.getOpeningHours() != null ? 
                restaurant.getOpeningHours() + " - " + restaurant.getClosingHours() : 
                "30-45 min");
        
        // Get menu items if available
        if (restaurant.getMenu() != null && restaurant.getMenu().getFoods() != null) {
            dto.setMenu(
                    restaurant.getMenu().getFoods().stream()
                            .map(food -> food.getName())
                            .collect(Collectors.toList())
            );
        } else {
            dto.setMenu(new ArrayList<>());
        }
        
        return dto;
    }

    @Override
    public List<RestaurantResponseDto> getUserFavorites(User user) {
        logger.debug("Getting favorite restaurants for user: {}", user.getEmail());
        
        List<RestaurantResponseDto> favorites = new ArrayList<>();
        
        // Convert user's favorite restaurant DTOs to response DTOs
        if (user.getFavourites() != null && !user.getFavourites().isEmpty()) {
            for (RestaurantDto favoriteDto : user.getFavourites()) {
                if (favoriteDto.getId() != null) {
                    try {
                        Restaurant restaurant = findById(favoriteDto.getId());
                        favorites.add(convertToDto(restaurant));
                    } catch (Exception e) {
                        // Skip restaurants that no longer exist
                        logger.warn("Favorite restaurant with ID {} no longer exists", favoriteDto.getId());
                    }
                }
            }
        }
        
        return favorites;
    }
}