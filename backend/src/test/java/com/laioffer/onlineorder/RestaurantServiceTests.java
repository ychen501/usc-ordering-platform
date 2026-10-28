package com.laioffer.onlineorder;

import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.RestaurantEntity;
import com.laioffer.onlineorder.model.RestaurantDto;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.RestaurantRepository;
import com.laioffer.onlineorder.service.RestaurantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class
RestaurantServiceTests {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    private RestaurantService restaurantService;

    @BeforeEach
    void setup() {
        restaurantService = new RestaurantService(restaurantRepository, menuItemRepository);
    }

    @Test
    void getRestaurants_whenNoData_shouldReturnEmptyList() {
        // Mock data
        Mockito.when(restaurantRepository.findAll()).thenReturn(List.of());
        Mockito.when(menuItemRepository.findAll()).thenReturn(List.of());

        // Perform the method under test
        List<RestaurantDto> result = restaurantService.getRestaurants();

        // Verify result
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getRestaurants_whenRestaurantHasNoMenuItems_shouldReturnRestaurantWithNullMenuItems() {
        // Mock data
        RestaurantEntity restaurant = new RestaurantEntity(1L, "Pizza Place", "123 Main St", "555-0001", "pizza.jpg");

        Mockito.when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));
        Mockito.when(menuItemRepository.findAll()).thenReturn(List.of());

        // Perform the method under test
        List<RestaurantDto> result = restaurantService.getRestaurants();

        // Verify result
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).id());
        Assertions.assertEquals("Pizza Place", result.get(0).name());
        Assertions.assertNull(result.get(0).menuItems());
    }

    @Test
    void getRestaurants_whenRestaurantHasMenuItems_shouldReturnGroupedMenuItems() {
        // Mock data
        RestaurantEntity restaurant = new RestaurantEntity(1L, "Pizza Place", "123 Main St", "555-0001", "pizza.jpg");
        MenuItemEntity item1 = new MenuItemEntity(1L, 1L, "Margherita", "Classic pizza", 12.0, "margherita.jpg");
        MenuItemEntity item2 = new MenuItemEntity(2L, 1L, "Pepperoni", "Spicy pizza", 14.0, "pepperoni.jpg");

        Mockito.when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));
        Mockito.when(menuItemRepository.findAll()).thenReturn(List.of(item1, item2));

        // Perform the method under test
        List<RestaurantDto> result = restaurantService.getRestaurants();

        // Verify result
        Assertions.assertEquals(1, result.size());
        RestaurantDto dto = result.get(0);
        Assertions.assertEquals(1L, dto.id());
        Assertions.assertNotNull(dto.menuItems());
        Assertions.assertEquals(2, dto.menuItems().size());
        Assertions.assertEquals("Margherita", dto.menuItems().get(0).name());
        Assertions.assertEquals("Pepperoni", dto.menuItems().get(1).name());
    }

    @Test
    void getRestaurants_whenMultipleRestaurants_shouldGroupMenuItemsByRestaurant() {
        // Mock data
        RestaurantEntity restaurant1 = new RestaurantEntity(1L, "Pizza Place", "123 Main St", "555-0001", "pizza.jpg");
        RestaurantEntity restaurant2 = new RestaurantEntity(2L, "Burger Bar", "456 Elm St", "555-0002", "burger.jpg");
        MenuItemEntity item1 = new MenuItemEntity(1L, 1L, "Margherita", "Classic pizza", 12.0, "margherita.jpg");
        MenuItemEntity item2 = new MenuItemEntity(2L, 2L, "Cheeseburger", "Classic burger", 9.0, "cheeseburger.jpg");
        MenuItemEntity item3 = new MenuItemEntity(3L, 2L, "Fries", "Crispy fries", 4.0, "fries.jpg");

        Mockito.when(restaurantRepository.findAll()).thenReturn(List.of(restaurant1, restaurant2));
        Mockito.when(menuItemRepository.findAll()).thenReturn(List.of(item1, item2, item3));

        // Perform the method under test
        List<RestaurantDto> result = restaurantService.getRestaurants();

        // Verify result
        Assertions.assertEquals(2, result.size());

        RestaurantDto pizzaDto = result.stream().filter(r -> r.id() == 1L).findFirst().orElseThrow();
        Assertions.assertEquals(1, pizzaDto.menuItems().size());
        Assertions.assertEquals("Margherita", pizzaDto.menuItems().get(0).name());

        RestaurantDto burgerDto = result.stream().filter(r -> r.id() == 2L).findFirst().orElseThrow();
        Assertions.assertEquals(2, burgerDto.menuItems().size());
    }

    @Test
    void getRestaurants_shouldCallRepositoryFindAllOnce() {
        // Mock data
        Mockito.when(restaurantRepository.findAll()).thenReturn(List.of());
        Mockito.when(menuItemRepository.findAll()).thenReturn(List.of());

        // Perform the method under test
        restaurantService.getRestaurants();

        // Verify repository interactions
        Mockito.verify(restaurantRepository).findAll();
        Mockito.verify(menuItemRepository).findAll();
    }
}
