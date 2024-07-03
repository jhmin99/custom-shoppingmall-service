package jihong99.shoppingmall.controller;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.dto.UserDetailsDto;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.enums.Roles;
import jihong99.shoppingmall.repository.UserRepository;
import jihong99.shoppingmall.service.IDeliveryAddressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DeliveryAddressControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IDeliveryAddressService deliveryAddressService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    /**
     * Tests the successful addition of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void addDeliveryAddress_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        Users savedUser = userRepository.save(user);
        savedUser.updateRole(Roles.USER);

        UserDetailsDto userDetailsDto = new UserDetailsDto(savedUser);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsDto, "password", userDetailsDto.getAuthorities()));
        SecurityContextHolder.setContext(context);

        DeliveryAddressDto addressDto = new DeliveryAddressDto(savedUser.getId(), null,"민지홍","01012341234"
        ,41412,"abc로 123", "101-1234");


        // when & then
        MockHttpServletRequestBuilder request = post("/api/users/delivery-address")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(addressDto));
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_201_createDeliveryAddress));
    }

    /**
     * Tests handling of a not found exception when user does not exist.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(username = "testuser")
    public void addDeliveryAddress_Return_NotFound_Handles_NotFoundException() throws Exception {
        // given
        DeliveryAddressDto addressDto = new DeliveryAddressDto(-1L, null,"민지홍","01012341234"
                ,41412,"abc로 123", "101-1234");
        // when & then
        MockHttpServletRequestBuilder request = post("/api/users/delivery-address")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addressDto));
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    /**
     * Tests the successful update of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    public void updateDeliveryAddress_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        Users savedUser = userRepository.save(user);
        DeliveryAddressDto addressDto = new DeliveryAddressDto(savedUser.getId(), null,"Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");
        deliveryAddressService.addDeliveryAddress(addressDto);
        Long addressId = deliveryAddressService.getDeliveryAddresses(user).stream().findAny().get().getId();

        // when & then
        MockHttpServletRequestBuilder request = put("/api/users/delivery-address/{addressId}", addressId.toString())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addressDto));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_UpdateDeliveryAddressSuccess));
    }

    /**
     * Tests handling of a not found exception when delivery address does not exist.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(username = "testuser")
    public void updateDeliveryAddress_Return_NotFound_Handles_NotFoundException() throws Exception {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        Users savedUser = userRepository.save(user);
        DeliveryAddressDto addressDto = new DeliveryAddressDto(savedUser.getId(), -1L,"Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");
        Long addressId = -1L;
        // when & then
        MockHttpServletRequestBuilder request = put("/api/users/delivery-address/{addressId}", addressId.toString())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addressDto));
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    /**
     * Tests the successful deletion of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    public void deleteDeliveryAddress_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        Users savedUser = userRepository.save(user);
        DeliveryAddressDto addressDto = new DeliveryAddressDto(savedUser.getId(), null,"Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");
        deliveryAddressService.addDeliveryAddress(addressDto);
        Long addressId = deliveryAddressService.getDeliveryAddresses(user).stream().findAny().get().getId();

        // when & then
        MockHttpServletRequestBuilder request = delete("/api/users/delivery-address/{addressId}", addressId.toString())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(addressDto));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_DeleteDeliveryAddressSuccess));
    }

    /**
     * Tests handling of a not found exception when delivery address does not exist.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    public void deleteDeliveryAddress_Return_NotFound_Handles_NotFoundException() throws Exception {
        Long addressId= -1L;
        // when & then
        MockHttpServletRequestBuilder request = delete("/api/users/delivery-address/{addressId}", addressId.toString())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }
}
