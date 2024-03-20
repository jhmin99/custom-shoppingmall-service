package jihong99.shoppingmall.controller;
import jakarta.transaction.Transactional;
import jihong99.shoppingmall.constants.UserConstants;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.exception.GlobalExceptionHandler;
import jihong99.shoppingmall.repository.UserRepository;
import jihong99.shoppingmall.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }


    /**
     * Tests the successful verification of identification.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void verifyIdentification_Return_OK() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users/check-id")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_200_verifiedId));
    }


    /**
     * Tests handling of a bad request when encountering a duplicate identification exception.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void verifyIdentifiacation_Return_BadRequest_Handles_DuplicateIdentificationException() throws Exception {
        // given
        SignUpDto signUpDto1 = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto1);
        // when
        SignUpDto signUpDto2 = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // then
        mockMvc.perform(post("/api/users/check-id")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto2)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_400_duplicatedId));
    }

    /**
     * Tests handling of a bad request when encountering a MethodArgumentNotValidException for a non-null identification.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void verifyIdentification_Return_BadRequest_Handles_MethodArgumentNotValidException_NOTNULL() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto(null,"abcd123!@#",
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
        SignUpDto signUpDto = new SignUpDto(identification,"abcd123!@#",
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
    public void verifyIdentification_Return_InternalServerError() throws Exception {
        // given
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(null))
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
        SignUpDto signUpDto = new SignUpDto("abcd1234","abcd123!@#",
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
    public void signUp_Return_OK() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_201_createUser));
    }

    /**
     * Tests handling of a bad request when encountering a DuplicateIdentificationException.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    public void signUp_Return_BadRequest_Handles_DuplicateIdentificationException() throws Exception{
        // given
        SignUpDto signUpDto1 = new SignUpDto("abc123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto1);
        // when & then
        SignUpDto signUpDto2 = new SignUpDto("abc123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(asJsonString(signUpDto2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_400_duplicatedId));

    }

    /**
     * Tests handling of a bad request when encountering a PasswordMismatchException.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void signUp_Return_BadRequest_Handles_PasswordMismatchException() throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#",
                "a222123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_400_MissMatchPw));
    }
    /**
     * Tests handling of a bad request when encountering a DateTimeParseException during sign-up.
     * @throws Exception if an error occurs during the test.
     */
    @ParameterizedTest
    @ValueSource(strings = {"1999-15-30", "1999-12-32"})
    public void signUp_Return_BadRequest_Handles_DateTimeParseException(String birthDate) throws Exception {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", birthDate, "01012341234");
        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.statusMessage").value(UserConstants.MESSAGE_400_WrongBirthDate));
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
            "abcd123, abc123!@#, abc123!@#, 민지흉, 1999-12, 01012341234",               // birthDate @Pattern (Not YYYY-MM-DD)
            "abcd123, abc123!@#, abc123!@#, 민지흉, 1999-12-3, 01012341234",             // birthDate @Pattern (Not YYYY-MM-DD)
            "abcd123, abc123!@#, abc123!@#, 민지흉, 99-12-30, 01012341234",              // birthDate @Pattern (Not YYYY-MM-DD)
            "abcd123, abc123!@#, abc123!@#, 민지흉, '', 01012341234",                    // birthDate @NotNull (null)
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
        mockMvc.perform(post("/api/users")
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
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(asJsonString(signUpDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof Exception));
    }

}