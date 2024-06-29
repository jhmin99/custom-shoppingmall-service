package jihong99.shoppingmall.controller;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.constants.UserConstants;
import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.GlobalExceptionHandler;
import jihong99.shoppingmall.repository.UserRepository;
import jihong99.shoppingmall.service.IDeliveryAddressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DeliveryAddressControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private IDeliveryAddressService deliveryAddressService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new DeliveryAddressController(deliveryAddressService))
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(GlobalExceptionHandler.class)
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
        userRepository.save(user);

        DeliveryAddressDto addressDto = new DeliveryAddressDto(user.getId(), null,"민지홍","01012341234"
        ,41412,"abc로 123", "101-1234");

        // when & then
        mockMvc.perform(post("/api/users/delivery-address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_201_createDeliveryAddress));
    }

    /**
     * Tests handling of a not found exception when user does not exist.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void addDeliveryAddress_Return_NotFound_Handles_UserNotFoundException() throws Exception {
        // given
        DeliveryAddressDto addressDto = new DeliveryAddressDto(-1L, null,"민지홍","01012341234"
                ,41412,"abc로 123", "101-1234");
        // when & then
        mockMvc.perform(post("/api/users/delivery-address")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_404_NoUserFound));
    }

    /**
     * Tests the successful update of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void updateDeliveryAddress_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);
        DeliveryAddressDto addressDto = new DeliveryAddressDto(user.getId(), null,"Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");
        deliveryAddressService.addDeliveryAddress(addressDto);
        Long addressId = deliveryAddressService.getDeliveryAddresses(user).stream().findAny().get().getId();

        // when & then
        mockMvc.perform(put("/api/users/delivery-address/{addressId}", addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_200_UpdateDeliveryAddressSuccess));
    }

    /**
     * Tests handling of a not found exception when delivery address does not exist.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void updateDeliveryAddress_Return_NotFound_Handles_DeliveryAddressNotFoundException() throws Exception {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);
        DeliveryAddressDto addressDto = new DeliveryAddressDto(1L, -1L,"Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");

        // when & then
        mockMvc.perform(put("/api/users/delivery-address/{addressId}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_404_NoDeliveryAddressFound));
    }

    /**
     * Tests the successful deletion of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void deleteDeliveryAddress_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);
        DeliveryAddressDto addressDto = new DeliveryAddressDto(user.getId(), null,"Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");
        deliveryAddressService.addDeliveryAddress(addressDto);
        Long addressId = deliveryAddressService.getDeliveryAddresses(user).stream().findAny().get().getId();

        // when & then
        mockMvc.perform(delete("/api/users/delivery-address/{addressId}", addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_200_DeleteDeliveryAddressSuccess));
    }

    /**
     * Tests handling of a not found exception when delivery address does not exist.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void deleteDeliveryAddress_Return_NotFound_Handles_DeliveryAddressNotFoundException() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/users/delivery-address/{addressId}", -1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_404_NoDeliveryAddressFound));
    }
}
