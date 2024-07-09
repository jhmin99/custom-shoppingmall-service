package jihong99.shoppingmall.controller;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserRepository;
import jihong99.shoppingmall.service.IUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static jihong99.shoppingmall.constants.Constants.*;
import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@ActiveProfiles("test")
class SuperAdminControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;
  
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @AfterEach
    void tearDown() {
        deliveryAddressRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    @Transactional
    @WithMockUser(username = "superadmin", roles = "SUPER_ADMIN")
    void createAdmin_Return_Created() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("admin123", "admin123!@#",
                "admin123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        MockHttpServletRequestBuilder request = post("/api/super-admin/create-admin")
                .contentType("application/json")
                .content(asJsonString(signUpDto))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMessage").value(MESSAGE_201_createUser));
    }

    @Test
    @Transactional
    @WithMockUser(username = "superadmin", roles = "SUPER_ADMIN")
    public void createAdmin_Return_BadRequest_Handles_DuplicateIdentificationException() throws Exception {
        // given
        SignUpDto signUpDto1 = new SignUpDto("admin123", "admin123!@#",
                "admin123!@#", "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto1);
        // when & then
        SignUpDto signUpDto2 = new SignUpDto("admin123", "admin123!@#",
                "admin123!@#", "민지홍", "1999-12-30", "01012341234");

        MockHttpServletRequestBuilder request = post("/api/super-admin/create-admin")
                .contentType("application/json")
                .content(asJsonString(signUpDto2))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorMessage").value(MESSAGE_400_duplicatedId));
    }

    @Test
    @WithMockUser(username = "superadmin", roles = "SUPER_ADMIN")
    public void signUp_Return_BadRequest_Handles_PasswordMismatchException() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("admin123", "admin123!@#",
                "wrong123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        MockHttpServletRequestBuilder request = post("/api/super-admin/create-admin")
                .contentType("application/json")
                .content(asJsonString(signUpDto))
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorMessage").value(MESSAGE_400_MisMatchPw));
    }
}