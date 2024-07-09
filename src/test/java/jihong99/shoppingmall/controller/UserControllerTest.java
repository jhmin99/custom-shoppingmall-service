package jihong99.shoppingmall.controller;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.dto.LoginRequestDto;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.dto.UserDetailsDto;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.enums.Roles;
import jihong99.shoppingmall.exception.GlobalExceptionHandler;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.UserRepository;
import jihong99.shoppingmall.service.IDeliveryAddressService;
import jihong99.shoppingmall.service.IUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static jihong99.shoppingmall.constants.Constants.*;
import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IDeliveryAddressService deliveryAddressService;

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
     * Tests the successful verification of identification.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void verifyIdentification_Return_OK() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users/check-id")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(MESSAGE_200_verifiedId));
    }

    /**
     * Tests handling of a bad request when encountering a duplicate identification exception.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void verifyIdentifiacation_Return_BadRequest_Handles_DuplicateIdentificationException() throws Exception {
        // given
        SignUpDto signUpDto1 = new SignUpDto("abcd123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto1);
        // when
        SignUpDto signUpDto2 = new SignUpDto("abcd123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // then
        mockMvc.perform(post("/api/users/check-id")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorMessage").value(MESSAGE_400_duplicatedId));
    }

    /**
     * Tests handling of a bad request when encountering a MethodArgumentNotValidException for a non-null identification.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void verifyIdentification_Return_BadRequest_Handles_MethodArgumentNotValidException_NOTNULL() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto(null, "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users/check-id")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.identification").value("ID is a required field."));
    }

    /**
     * Tests handling of a bad request when encountering a MethodArgumentNotValidException for identification pattern validation.
     * @throws Exception if an error occurs during the test.
     */
    @ParameterizedTest
    @ValueSource(strings = {"abcdefg", "123456", "@@@123", "abcdefg1234", "abc12", "@ab123"})
    public void verifyIdentification_Return_BadRequest_Handles_MethodArgumentNotValidException_Pattern(String identification) throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto(identification, "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users/check-id")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.identification").value("ID must consist of alphabets and numbers, containing both, with a length of 6-10 characters."));
    }

    /**
     * Tests handling of an internal server error.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void verifyIdentification_InternalServerError_Handles_Exception() throws Exception {
        // given
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(null))
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
        SignUpDto signUpDto = new SignUpDto("abcd1234", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users/check-id")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof Exception));
    }

    /**
     * Tests the successful sign-up process.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void signUp_Return_Created() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusMessage").value(MESSAGE_201_createUser));
    }

    /**
     * Tests handling of a bad request when encountering a DuplicateIdentificationException.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void signUp_Return_BadRequest_Handles_DuplicateIdentificationException() throws Exception {
        // given
        SignUpDto signUpDto1 = new SignUpDto("abc123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto1);
        // when & then
        SignUpDto signUpDto2 = new SignUpDto("abc123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");

        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorMessage").value(MESSAGE_400_duplicatedId));
    }

    /**
     * Tests handling of a bad request when encountering a PasswordMismatchException.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void signUp_Return_BadRequest_Handles_PasswordMismatchException() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#",
                "a222123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorMessage").value(MESSAGE_400_MisMatchPw));
    }

    /**
     * Tests handling of a bad request when encountering a DateTimeParseException during sign-up.
     * @throws Exception if an error occurs during the test.
     */
    @ParameterizedTest
    @ValueSource(strings = {"1999-15-30", "1999-12-32"})
    public void signUp_Return_BadRequest_Handles_DateTimeParseException(String birthDate) throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#",
                "abcd123!@#", "민지홍", birthDate, "01012341234");
        // when & then
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.name()))
                .andExpect(jsonPath("$.errorMessage").value("Invalid date format: " + birthDate));
    }

    /**
     * Tests handling of a bad request when encountering a MethodArgumentNotValidException during sign-up.
     * @throws Exception if an error occurs during the test.
     * @param (identification, password, confirmPassword, name, birthDate, phoneNumber)
     */
    @ParameterizedTest
    @CsvSource({
            "'', '', '', '', '', ''",                                                  // all @NutNull (null)
            "abc12, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",              // identification @Pattern (length < 6)
            "abc12@, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",             // identification @Pattern (include not an alphabet nor number)
            "abcabc, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",             // identification @Pattern (not include any number)
            "123123, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",             // identification @Pattern (not include any alphabet)
            "abcde123456, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",        // identification @Pattern (length > 10)
            "'', abc123!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",                 // identification @NotNull (null)
            "abcd123, abc123!, abc123!@#, 민지홍, 1999-12-30, 01012341234",              // password @Pattern (length < 8)
            "abcd123, abc12345, abc123!@#, 민지홍, 1999-12-30, 01012341234",             // password @Pattern (not include any special character)
            "abcd123, 12345!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",             // password @Pattern (not include any alphabet)
            "abcd123, abcde!@#, abc123!@#, 민지홍, 1999-12-30, 01012341234",             // password @Pattern (not include any number)
            "abcd123, abc123?], abc123!@#, 민지홍, 1999-12-30, 01012341234",             // password @Pattern (include not allowed special characters)
            "abcd123, abc123!@#1234567, abc123!@#, 민지홍, 1999-12-30, 01012341234",     // password @Pattern (length > 15)
            "abcd123, '', abc123!@#, 민지홍, 1999-12-30, 01012341234",                   // password @NotNull (null)
            "abcd123, abc123!@#, '', 민지홍, 1999-12-30, 01012341234",                   // confirmPassword @NotNull (null)
            "abcd123, abc123!@#, abc123!@#, '', 1999-12-30, 01012341234",              // name @NotNull (null)
            "abcd123, abc123!@#, abc123!@#, 민지홍, 1999-12, 01012341234",               // birthDate @Pattern (Not YYYY-MM-DD)
            "abcd123, abc123!@#, abc123!@#, 민지홍, 1999-12-3, 01012341234",             // birthDate @Pattern (Not YYYY-MM-DD)
            "abcd123, abc123!@#, abc123!@#, 민지홍, 99-12-30, 01012341234",              // birthDate @Pattern (Not YYYY-MM-DD)
            "abcd123, abc123!@#, abc123!@#, 민지홍, '', 01012341234",                    // birthDate @NotNull (null)
            "abcd123, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 010444222",              // phoneNumber @Pattern (length < 11)
            "abcd123, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 01044422abc",            // phoneNumber @Pattern (include not a number)
            "abcd123, abc123!@#, abc123!@#, 민지홍, 1999-12-30, 010123412341",           // phoneNumber @Pattern (length > 11)
            "abcd123, abc123!@#, abc123!@#, 민지홍, 1999-12-30, ''",                     // phoneNumber @NotNull (null)
    })
    public void signUp_Return_BadRequest_Handles_MethodArgumentNotValidException(String identification, String password, String confirmPassword, String name, String birthDate, String phoneNumber) throws Exception {
        // given
        String actualIdentification = identification.isEmpty() ? null : identification;
        String actualPassword = password.isEmpty() ? null : password;
        String actualConfirmPassword = confirmPassword.isEmpty() ? null : confirmPassword;
        String actualName = name.isEmpty() ? null : name;
        String actualBirthDate = birthDate.isEmpty() ? null : birthDate;
        String actualPhoneNumber = phoneNumber.isEmpty() ? null : phoneNumber;

        SignUpDto signUpDto = new SignUpDto(actualIdentification, actualPassword, actualConfirmPassword, actualName, actualBirthDate, actualPhoneNumber);
        // when & then
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    /**
     * Tests handling of an internal server error during sign-up.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void signUp_Return_InternalServerError_Handles_Exception() throws Exception {
        // given
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(null))
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/signup")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof Exception));
    }

    /**
     * Tests the successful login process.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void login_Return_OK() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto);

        LoginRequestDto loginRequestDto = new LoginRequestDto("abcd123", "abcd123!@#");

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(asJsonString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    /**
     * Tests handling of a bad request when encountering bad credentials during login.
     * @throws Exception if an error occurs during the test.
     */
    @ParameterizedTest
    @CsvSource({
            "'', 'password'",         // empty identification
            "'abcd123', ''",          // empty password
            "'wrongId', 'wrongPassword'" // wrong credentials
    })
    @Transactional
    public void login_Return_BadRequest_Handles_BadCredentialsException(String identification, String password) throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto(identification, password);

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType("application/json")
                        .content(asJsonString(loginRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException));
    }

    /**
     * Tests the successful logout process.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(username = "testuser")
    public void logout_Return_OK() throws Exception {
        // Perform logout and check the response
        MockHttpServletRequestBuilder request = post("/api/logout")
                .with(csrf());
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(MESSAGE_200_LogoutSuccess));
    }

    /**
     * Tests handling when no user is logged in during logout.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void logout_Return_Forbidden_WhenNotLoggedIn() throws Exception {
        // Clear the security context to simulate no user logged in
        SecurityContextHolder.clearContext();
        // Perform logout and check the response
        mockMvc.perform(post("/api/logout"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests the successful retrieval of user details.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    void getUserDetails_Return_OK() throws Exception {
        // given
        Users testUser = Users.builder()
                .identification("testuser")
                .name("Test User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("01012345678")
                .build();
        userRepository.save(testUser);

        Long userId = testUser.getId();

        // when & then
        mockMvc.perform(get("/api/users")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identification").value("testuser"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    /**
     * Tests handling of a not found error when the user does not exist.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(username = "testuser")
    void getUserDetails_Return_NotFound_Handles_NotFoundException() throws Exception {
        // given
        Long userId = 3L; // This user does not exist

        // when & then
        mockMvc.perform(get("/api/users")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(NOT_FOUND.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    /**
     * Tests handling of an internal server error when the service is null.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "testuser")
    void getUserDetails_Return_InternalServerError() throws Exception {
        // given
        Users testUser = Users.builder()
                .identification("testuser")
                .name("Test User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("01012345678")
                .build();
        userRepository.save(testUser);

        Long userId = testUser.getId();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(null))
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
        // when & then
        mockMvc.perform(get("/api/users")
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Transactional
    public void getUserDetails_Return_Forbidden_WhenLoggedInAsDifferentUser() throws Exception {
        // given
        Users testUser = Users.builder()
                .identification("testuser")
                .name("Test User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("01012345678")
                .build();
        userRepository.save(testUser);

        Users otherUser = Users.builder()
                .identification("otheruser")
                .name("Other User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("01012345678")
                .build();
        Users savedUser = userRepository.save(otherUser);
        savedUser.updateRole(Roles.USER);

        UserDetailsDto userDetailsDto = new UserDetailsDto(savedUser);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsDto, "password", userDetailsDto.getAuthorities()));
        SecurityContextHolder.setContext(context);

        // when & then
        mockMvc.perform(get("/api/users")
                        .param("userId", testUser.getId().toString()))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests the successful retrieval of user summaries by an admin.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getUsers_Return_OK_ForAdmin_Without_DeliveryAddress() throws Exception {
        // given
        Users adminUser = Users.builder()
                .identification("admin")
                .name("Admin User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("01012345678")
                .build();
        Users savedAdmin = userRepository.save(adminUser);
        savedAdmin.updateRole(Roles.ADMIN);

        Users normalUser = Users.builder()
                .identification("user1")
                .name("User One")
                .birthDate(LocalDate.of(1991, 1, 1))
                .phoneNumber("01087654321")
                .build();
        Users savedUser = userRepository.save(normalUser);
        savedUser.updateRole(Roles.USER);
        // when & then
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(savedUser.getId()))
                .andExpect(jsonPath("$.content[0].name").value(savedUser.getName()))
                .andExpect(jsonPath("$.content[0].birthDate").value(savedUser.getBirthDate().toString()))
                .andExpect(jsonPath("$.content[0].address").doesNotExist())
                .andExpect(jsonPath("$.content[0].registrationDate").value(savedUser.getRegistrationDate().toString()));

    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void getUsers_Return_OK_ForAdmin_With_DeliveryAddress() throws Exception {
        // given
        Users adminUser = Users.builder()
                .identification("admin")
                .name("Admin User")
                .birthDate(LocalDate.of(1990, 1, 1))
                .phoneNumber("01012345678")
                .build();
        Users savedAdmin = userRepository.save(adminUser);
        savedAdmin.updateRole(Roles.ADMIN);

        Users normalUser = Users.builder()
                .identification("user1")
                .name("User One")
                .birthDate(LocalDate.of(1991, 1, 1))
                .phoneNumber("01087654321")
                .build();
        Users savedUser = userRepository.save(normalUser);
        savedUser.updateRole(Roles.USER);

        Long addressId = deliveryAddressService.addDeliveryAddress(new DeliveryAddressDto(savedUser.getId(), null, "abc","01012341234",
                12345,"abc로 123", "101- 1234"));
        // when & then
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(savedUser.getId()))
                .andExpect(jsonPath("$.content[0].name").value(savedUser.getName()))
                .andExpect(jsonPath("$.content[0].birthDate").value(savedUser.getBirthDate().toString()))
                .andExpect(jsonPath("$.content[0].addressId").value(addressId))
                .andExpect(jsonPath("$.content[0].zipCode").value(12345))
                .andExpect(jsonPath("$.content[0].address").value("abc로 123"))
                .andExpect(jsonPath("$.content[0].addressDetail").value("101- 1234"))
                .andExpect(jsonPath("$.content[0].registrationDate").value(savedUser.getRegistrationDate().toString()));

    }
    /**
     * Tests forbidden access when a non-admin user tries to retrieve user summaries.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "user", roles = {"USER"})
    public void getUsers_Return_Forbidden_ForNonAdmin() throws Exception {
        // given
        Users normalUser = Users.builder()
                .identification("user")
                .name("Normal User")
                .birthDate(LocalDate.of(1991, 1, 1))
                .phoneNumber("01087654321")
                .build();
        userRepository.save(normalUser);

        // when & then
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}

