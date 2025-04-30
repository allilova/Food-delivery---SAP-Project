package com.example.food_delivery_app.service;

import com.example.food_delivery_app.dto.RestaurantDto;
import com.example.food_delivery_app.dto.RestaurantResponseDto;
import com.example.food_delivery_app.model.Address;
import com.example.food_delivery_app.model.Restaurant;
import com.example.food_delivery_app.model.User;
import com.example.food_delivery_app.repository.AddressRepository;
import com.example.food_delivery_app.repository.RestaurantRepository;
import com.example.food_delivery_app.repository.UserRepository;
import com.example.food_delivery_app.request.CreateRestaurantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<RestaurantResponseDto> getAllRestaurantsDto() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::convertToDto)
                .toList();
    }
    @Override
    public List<RestaurantResponseDto> searchRestaurantsDto(String keyword) {
        List<Restaurant> restaurants = restaurantRepository.findBySearchQuery(keyword);
        return restaurants.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Restaurant createRestaurant(CreateRestaurantRequest req, User user) {

        Address address = addressRepository.save(req.getRestaurantAddress());

        Restaurant restaurant = new Restaurant();

        restaurant.setRestaurantAddress(address);
        restaurant.setContactInfo(req.getContactInfo());
        restaurant.setType(req.getType());
        restaurant.setClosingHours(req.getClosingHours());
        restaurant.setOpeningHours(req.getOpeningHours());
        restaurant.setImages(req.getImages());
        restaurant.setRestaurantName(req.getRestaurantName());
        restaurant.setRestaurant(user);

        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long restaurantId, CreateRestaurantRequest updatesRestaurant) throws Exception {

        Restaurant restaurant = findById(restaurantId);

        if (restaurant.getType() != null) {
            restaurant.setType(updatesRestaurant.getType());
        }
        if (restaurant.getRestaurantName() != null) {
            restaurant.setRestaurantName(updatesRestaurant.getRestaurantName());
        }
        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteRestaurant(Long restaurantId) throws Exception {

        Restaurant restaurant = findById(restaurantId);

        restaurantRepository.delete(restaurant);

    }


    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> searchRestaurants(String keyword) {
        return restaurantRepository.findBySearchQuery(keyword);
    }


    @Override
    public Restaurant findById(Long id) throws Exception {

        Optional<Restaurant> opt = restaurantRepository.findById(id);

        if (opt.isEmpty()) {
            throw new Exception("Restaurant not found with id " + id);
        }
        return opt.get();
    }

    @Override
    public RestaurantDto addToFavourites(Long restaurantId, User user) throws Exception {
        Restaurant restaurant = findById(restaurantId);

        RestaurantDto dto = new RestaurantDto();
        dto.setRestaurantName(restaurant.getRestaurantName());
        dto.setImages(restaurant.getImages());
        dto.setId(restaurantId);
        dto.setAddress(restaurant.getRestaurantAddress().toString());
        dto.setEmail(restaurant.getContactInfo().getEmail());

        boolean exists = user.getFavourites().stream()
                .anyMatch(fav -> fav.getId().equals(dto.getId()));

        if (exists) {
            user.getFavourites().removeIf(fav -> fav.getId().equals(dto.getId()));
        } else {
            user.getFavourites().add(dto);
        }

        userRepository.save(user);
        return dto;
    }

    @Override
    public Restaurant updateRestaurantStatus(Long restaurantId) throws Exception {
        Restaurant restaurant = findById(restaurantId);

        restaurant.setOpen(!restaurant.isOpen());
        return restaurantRepository.save(restaurant);
    }
    public RestaurantResponseDto convertToDto(Restaurant restaurant) {
        RestaurantResponseDto dto = new RestaurantResponseDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getRestaurantName());
        dto.setImgUrl(
                restaurant.getImages() != null && !restaurant.getImages().isEmpty()
                        ? restaurant.getImages().get(0)
                        : "default-restaurant.jpg"
        );
        dto.setAddress(restaurant.getRestaurantAddress() != null
                ? restaurant.getRestaurantAddress().toString()
                : "");
        dto.setRating(4.5); // Може да го изчислиш по-късно от restaurant.getRatings()
        dto.setFoodType(restaurant.getType());
        dto.setTimeDelivery("30-45 min");

        if (restaurant.getMenu() != null && restaurant.getMenu().getFoods() != null) {
            dto.setMenu(
                    restaurant.getMenu().getFoods().stream()
                            .map(food -> food.getFoodName())
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
//    public Optional<RestaurantResponseDto> getRestaurantById(Long id) {
//        return restaurantRepository.findById(id)
//                .map(this::convertToDto);
//    }
//}
//
//    private RestaurantResponseDto convertToDto(Restaurant restaurant) {
//        RestaurantResponseDto dto = new RestaurantResponseDto();
//        dto.setRestaurantId(restaurant.getRestaurantID().toString());
//        dto.setName(restaurant.getRestaurantName());
//        // Use the first image from the images list or a default
//        dto.setImgUrl(restaurant.getImages() != null && !restaurant.getImages().isEmpty()
//                ? restaurant.getImages().get(0)
//                : "default-restaurant.jpg");
//        dto.setAddress(restaurant.getRestaurantAddress() != null
//                ? restaurant.getRestaurantAddress().toString()
//                : "");
//        // Since rating isn't directly in the model, we could calculate it or use a default
//        dto.setRating(4.5); // Default or could be calculated from ratings
//        dto.setFoodType(restaurant.getType());
//        // Calculate or set a default delivery time
//        dto.setTimeDelivery("30-45 min");
//
//        // Extract menu items if available
//        List<String> menuItems = new ArrayList<>();
//        if (restaurant.getMenu() != null && restaurant.getMenu().getFoods() != null) {
//            menuItems = restaurant.getMenu().getFoods().stream()
//                    .map(food -> food.getFoodName())
//                    .collect(Collectors.toList());
//        }
//        dto.setMenu(menuItems);
//
//        return dto;
//    }
