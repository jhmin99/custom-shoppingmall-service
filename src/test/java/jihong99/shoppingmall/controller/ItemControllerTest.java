package jihong99.shoppingmall.controller;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.exception.GlobalExceptionHandler;
import jihong99.shoppingmall.repository.CategoryItemRepository;
import jihong99.shoppingmall.repository.CategoryRepository;
import jihong99.shoppingmall.repository.ItemRepository;
import jihong99.shoppingmall.service.ICategoryService;
import jihong99.shoppingmall.service.IItemService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ItemControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private IItemService itemService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryItemRepository categoryItemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");
        categoryService.createCategory(categoryRequestDto);
    }

    @AfterEach
    void tearDown() {
        categoryItemRepository.deleteAll();
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createItem_Return_Created() throws Exception{
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("sample1");
        Long categoryId = categoryService.createCategory(categoryRequestDto).getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto("sample", 100, 50, "#sample", Arrays.asList(categoryId));
        // when & then
        mockMvc.perform(post("/api/admin/item")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());

    }
    @ParameterizedTest
    @WithMockUser(username = "admin", roles = "ADMIN")
    @CsvSource({
            "null, 100, 10, #keyword, 1", // Name is null
            "'', 100, 10, #keyword, 1",  // Name is empty
            "ValidName, null, 10, #keyword, 1", // Price is null
            "ValidName, 100, null, #keyword, 1", // Inventory is null
            "ValidName, -1, 10, #keyword, 1",  // Price is negative
            "ValidName, 100, -1, #keyword, 1", // Inventory is negative
            "ValidName, 100, 10, '', 1",  // Keyword is empty
            "ValidName, 100, 10, invalidkeyword, 1", // Keyword does not start with #
            "ValidName, 100, 10, #toolongkeywordtoolongkeywordtoolongkeywordtoolongkeyword, 1", // Keyword too long
            "ValidName, 100, 10, #keyword, null", // Category IDs null
    })
    void createItem_Return_BadRequest_Handles_MethodArgumentNotValidException(String name, String price, String inventory, String keyword, String categoryIds) throws Exception {
        // given
        Integer actualPrice = "null".equals(price) ? null : Integer.valueOf(price);
        Integer actualInventory = "null".equals(inventory) ? null : Integer.valueOf(inventory);
        List<Long> actualCategoryIds = "null".equals(categoryIds) ? null :
                Arrays.stream(categoryIds.split(","))
                        .map(id -> "null".equals(id) ? null : Long.valueOf(id))
                        .collect(Collectors.toList());

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "null".equals(name) ? null : name,
                actualPrice,
                actualInventory,
                "null".equals(keyword) ? null : keyword,
                actualCategoryIds
        );

        // when & then
        mockMvc.perform(post("/api/admin/item")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    @Test
    @WithMockUser(username = "user", roles = "USER")
    void createItem_Return_Forbidden_NotAuthorized() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("sample1");
        Long categoryId = categoryService.createCategory(categoryRequestDto).getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto("sample", 100, 50, "#sample", Arrays.asList(categoryId));
        // when & then
        mockMvc.perform(post("/api/admin/item")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemRequestDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createItem_Return_InternalServerError_Handles_Exception() throws Exception{
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("sample1");
        Long categoryId = categoryService.createCategory(categoryRequestDto).getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto("sample", 100, 50, "#sample", Arrays.asList(categoryId));
        // when & then
        mockMvc = MockMvcBuilders.standaloneSetup(new ItemController(null))
                .setValidator(new LocalValidatorFactoryBean())
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
        // when & then
        mockMvc.perform(post("/api/admin/item")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemRequestDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateItem_Return_Ok() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");
        Long categoryId = categoryService.createCategory(categoryRequestDto).getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto("Sample Item", 1000, 10, "#sample", Arrays.asList(categoryId));
        Long itemId = itemService.createItem(itemRequestDto).getId();

        ItemRequestDto updatedRequestDto = new ItemRequestDto("Updated Item", 1500, 20, "#updated", Arrays.asList(categoryId));

        // when & then
        mockMvc.perform(put("/api/admin/item/{itemId}", itemId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_UpdateItemSuccess));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateItem_Return_NotFound() throws Exception {
        // given
        ItemRequestDto updatedRequestDto = new ItemRequestDto("Updated Item", 1500, 20, "#updated", Arrays.asList(1L));

        // when & then
        mockMvc.perform(put("/api/admin/item/{itemId}", -1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedRequestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteItem_Return_Ok() throws Exception {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");
        Long categoryId = categoryService.createCategory(categoryRequestDto).getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto("Sample Item", 1000, 10, "#sample", Arrays.asList(categoryId));
        Long itemId = itemService.createItem(itemRequestDto).getId();
        // when
        Assertions.assertThat(categoryItemRepository.findByItemId(itemId).size()).isEqualTo(1);
        // then
        mockMvc.perform(delete("/api/admin/item/{itemId}", itemId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_DeleteItemSuccess));
        Assertions.assertThat(categoryItemRepository.findByItemId(itemId).size()).isEqualTo(0);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteItem_Return_NotFound() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/admin/item/{itemId}", -1L)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }
}