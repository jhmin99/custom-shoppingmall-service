package jihong99.shoppingmall.controller;

import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.exception.GlobalExceptionHandler;
import jihong99.shoppingmall.repository.CategoryRepository;
import jihong99.shoppingmall.service.ICategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ICategoryService icategoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_Return_OK() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");

        // when & then
        MockHttpServletRequestBuilder request = post("/api/admin/category")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(categoryRequestDto));
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sample Category"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_Return_BadRequest_Handles_MethodArgumentNotValidException_NotNull() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto(null);

        // when & then
        MockHttpServletRequestBuilder request = post("/api/admin/category")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(categoryRequestDto));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.name").value("name is a required field."));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_Return_BadRequest_Handles_MethodArgumentNotValidException_Size() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Cat");

        // when & then
        MockHttpServletRequestBuilder request = post("/api/admin/category")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(categoryRequestDto));
        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.name").value("name must be between 5 and 20 characters."));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createCategory_Return_Forbidden_NotAuthorized() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");

        // when & then
        MockHttpServletRequestBuilder request = post("/api/admin/category")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(categoryRequestDto));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createCategory_Return_InternalServerError_Handles_Exception() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");
        MockHttpServletRequestBuilder request = post("/api/admin/category")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(categoryRequestDto));
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(null))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.INTERNAL_SERVER_ERROR.name()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getCategories_Return_OK() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");
        icategoryService.createCategory(categoryRequestDto);

        // when & then
        MockHttpServletRequestBuilder request = get("/api/categories")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_fetchSuccess));
    }

    @Test
    void getCategories_Return_InternalServerError_Handles_Exception() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(null))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.INTERNAL_SERVER_ERROR.name()));
    }

}
