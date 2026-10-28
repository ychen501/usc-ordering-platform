package com.laioffer.onlineorder;


import com.laioffer.onlineorder.entity.CartEntity;
import com.laioffer.onlineorder.entity.CustomerEntity;
import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.CustomerRepository;
import com.laioffer.onlineorder.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceTests {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsManager userDetailsManager;

    private CustomerService customerService;

    @BeforeEach
    void setup() {
        customerService = new CustomerService(cartRepository, customerRepository, passwordEncoder, userDetailsManager);
    }

    @Test
    void signUp_shouldCreateUserAndCart() {
        // Mock data
        String email = "Test@Example.com";
        String password = "secret";
        String firstName = "John";
        String lastName = "Doe";
        String encodedPassword = "encoded_secret";
        CustomerEntity savedCustomer = new CustomerEntity(10L, "test@example.com", encodedPassword, true, firstName, lastName);

        // Mock repository/service calls
        Mockito.when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        Mockito.when(customerRepository.findByEmail("test@example.com")).thenReturn(savedCustomer);

        // Perform the method under test
        customerService.signUp(email, password, firstName, lastName);

        // Verify email is lowercased when creating the Spring Security user
        Mockito.verify(userDetailsManager).createUser(Mockito.argThat((UserDetails u) ->
                u.getUsername().equals("test@example.com")
                        && u.getPassword().equals(encodedPassword)
        ));

        // Verify customer name is updated in the repository
        Mockito.verify(customerRepository).updateNameByEmail("test@example.com", firstName, lastName);

        // Verify a cart is created for the new customer
        CartEntity expectedCart = new CartEntity(null, savedCustomer.id(), 0.0);
        Mockito.verify(cartRepository).save(expectedCart);
    }

    @Test
    void signUp_shouldLowercaseEmail() {
        // Mock data
        String mixedCaseEmail = "USER@DOMAIN.COM";
        String expectedEmail = "user@domain.com";
        CustomerEntity savedCustomer = new CustomerEntity(1L, expectedEmail, "encoded", true, "Jane", "Smith");

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded");
        Mockito.when(customerRepository.findByEmail(expectedEmail)).thenReturn(savedCustomer);

        // Perform the method under test
        customerService.signUp(mixedCaseEmail, "pass", "Jane", "Smith");

        // Verify all downstream calls use lowercased email
        Mockito.verify(customerRepository).updateNameByEmail(Mockito.eq(expectedEmail), Mockito.anyString(), Mockito.anyString());
        Mockito.verify(customerRepository).findByEmail(expectedEmail);
    }

    @Test
    void getCustomerByEmail_shouldReturnCustomer() {
        // Mock data
        String email = "john@example.com";
        CustomerEntity expected = new CustomerEntity(5L, email, "encoded", true, "John", "Doe");

        Mockito.when(customerRepository.findByEmail(email)).thenReturn(expected);

        // Perform the method under test
        CustomerEntity result = customerService.getCustomerByEmail(email);

        // Verify the result
        Assertions.assertEquals(expected, result);
        Mockito.verify(customerRepository).findByEmail(email);
    }
}
