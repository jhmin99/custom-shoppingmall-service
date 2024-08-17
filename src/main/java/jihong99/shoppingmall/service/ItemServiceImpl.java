package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.request.item.ItemRequestDto;
import jihong99.shoppingmall.dto.request.item.PatchItemRequestDto;
import jihong99.shoppingmall.dto.request.item.UpdateStockRequestDto;
import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.entity.CategoryItem;
import jihong99.shoppingmall.entity.Image;
import jihong99.shoppingmall.entity.Item;
import jihong99.shoppingmall.exception.ImageUploadException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.CategoryItemRepository;
import jihong99.shoppingmall.repository.CategoryRepository;
import jihong99.shoppingmall.repository.ImageRepository;
import jihong99.shoppingmall.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements IItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryItemRepository categoryItemRepository;
    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    /**
     * Creates a new item with the provided details and images.
     *
     * @param itemRequestDto DTO containing item details such as name, price, stock, and categories
     * @param images A list of images associated with the item
     * @throws NotFoundException if any category ID provided does not exist
     * @throws ImageUploadException if any image fails to upload
     * @throws IllegalArgumentException if the storage type for file uploads is invalid
     */
    @Transactional
    @Override
    public void createItem(ItemRequestDto itemRequestDto, List<MultipartFile> images) {
        validateCategoriesExist(itemRequestDto.getCategoryIds());
        List<Image> imageEntities = uploadAndSaveImages(images);
        Item item = buildItemFromRequest(itemRequestDto, imageEntities);
        saveCategoryItems(itemRequestDto.getCategoryIds(), item);
    }

    /**
     * Validates that all provided category IDs exist in the database.
     *
     * @param categoryIds List of category IDs to validate
     * @throws NotFoundException if any category ID does not exist
     */
    private void validateCategoriesExist(List<Long> categoryIds) {
        categoryIds.forEach(categoryId -> {
            if (!categoryRepository.existsById(categoryId)) {
                throw new NotFoundException(String.format(MESSAGE_404_CategoryNotFound + " " + categoryId));
            }
        });
    }

    /**
     * Uploads and saves images to the storage and returns their entity representations.
     *
     * @param images List of MultipartFile objects representing the images to be uploaded
     * @return List of Image entities representing the uploaded images
     * @throws ImageUploadException if any image fails to upload
     */
    private List<Image> uploadAndSaveImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();  // Return an empty list if no images are provided
        }
        return images.stream()
                .map(this::uploadAndSaveSingleImage)  // Upload and save each image
                .collect(Collectors.toList());  // Collect the results into a list
    }

    /**
     * Uploads and saves a single image to the storage and database.
     *
     * @param image MultipartFile representing the image to be uploaded
     * @return Image entity representing the uploaded image
     * @throws ImageUploadException if the image fails to upload
     */
    private Image uploadAndSaveSingleImage(MultipartFile image) {
        try {
            String imageUrl = fileStorageService.uploadFile(image.getOriginalFilename(), image.getInputStream(), image.getContentType());
            Image savedImage = Image.builder()
                    .name(image.getOriginalFilename())
                    .url(imageUrl)
                    .type(image.getContentType())
                    .size(image.getSize())
                    .build();
            return imageRepository.save(savedImage);
        } catch (IOException e) {
            throw new ImageUploadException(MESSAGE_500_ImageUploadFailed);
        }
    }

    /**
     * Builds an Item entity from the provided request data and image entities.
     *
     * @param itemRequestDto DTO containing item details such as name, price, stock, and keyword
     * @param imageEntities List of Image entities to associate with the item
     * @return The saved Item entity
     */
    private Item buildItemFromRequest(ItemRequestDto itemRequestDto, List<Image> imageEntities) {
        Item item = Item.builder()
                .name(itemRequestDto.getName())
                .price(itemRequestDto.getPrice())
                .stock(itemRequestDto.getStock())
                .keyword(itemRequestDto.getKeyword())
                .images(imageEntities)
                .build();
        return itemRepository.save(item);
    }

    /**
     * Saves the associations between the created item and its categories.
     *
     * @param categoryIds The category IDs
     * @param item The Item entity to be associated with the categories
     */
    private void saveCategoryItems(List<Long> categoryIds, Item item) {
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(MESSAGE_404_CategoryNotFound));
            CategoryItem categoryItem = getCategoryItem(item, category);
            categoryItemRepository.save(categoryItem);
        }
    }

    /**
     * Creates a CategoryItem entity representing the relationship between an item and a category.
     *
     * @param item The Item entity
     * @param category The Category entity
     * @return The created CategoryItem entity
     */
    private static CategoryItem getCategoryItem(Item item, Category category) {
        return CategoryItem.builder()
                .category(category)
                .item(item).build();
    }

    /**
     * Updates an existing item with the provided details, images to add, and images to remove.
     *
     * @param itemId The ID of the item to update
     * @param patchItemRequestDto DTO containing item details to update such as name, price, keyword, and categories
     * @param addImages List of images to add to the item
     * @param removeImageIds List of image IDs to remove from the item
     * @throws NotFoundException if the item, category, or image does not exist
     * @throws ImageUploadException if any image fails to upload
     * @throws IllegalArgumentException if the storage type for file uploads is invalid
     */
    @Override
    @Transactional
    public void patchItem(Long itemId, PatchItemRequestDto patchItemRequestDto, List<MultipartFile> addImages, List<Long> removeImageIds) {
        Item item = validateItemsExist(itemId);
        if (patchItemRequestDto.getName() != null && !patchItemRequestDto.getName().isEmpty()) {
            item.updateName(patchItemRequestDto.getName());
        }
        if (patchItemRequestDto.getPrice() != null) {
            item.updatePrice(patchItemRequestDto.getPrice());
        }
        if (patchItemRequestDto.getKeyword() != null && !patchItemRequestDto.getKeyword().isEmpty()) {
            item.updateKeyword(patchItemRequestDto.getKeyword());
        }
        if (patchItemRequestDto.getCategoryIds() != null && !patchItemRequestDto.getCategoryIds().isEmpty()) {
            updateItemCategories(item, patchItemRequestDto.getCategoryIds());
        }
        if (removeImageIds != null && !removeImageIds.isEmpty()) {
            removeImagesFromItem(item, removeImageIds);
        }
        if (addImages != null && !addImages.isEmpty()) {
            List<Image> imageEntities = uploadAndSaveImages(addImages);
            imageEntities.forEach(item::addImage);
        }
        itemRepository.save(item);
    }

    /**
     * Marks an item as invalid, effectively making it unavailable for purchase.
     *
     * @param id The ID of the item to be marked as invalid
     */
    @Override
    @Transactional
    public void markItemAsInvalid(Long id) {
        Item item = validateItemsExist(id);
        item.invalidateItem();
        itemRepository.save(item);
    }
    /**
     * Marks an item as valid, making it available for purchase again.
     *
     * @param id The ID of the item to be marked as valid
     */
    @Override
    @Transactional
    public void markItemAsValid(Long id) {
        Item item = validateItemsExist(id);
        item.validateItem();
        itemRepository.save(item);

    }

    /**
     * Updates the stock levels of an item.
     *
     * @param id The ID of the item whose stock is to be updated
     * @param updateStockRequestDto DTO containing the new stock level
     */
    @Override
    @Transactional
    public void updateItemStock(Long id, UpdateStockRequestDto updateStockRequestDto) {
        Item item = validateItemsExist(id);
        item.updateStock(updateStockRequestDto.getStock());
        itemRepository.save(item);
    }

    /**
     * Removes images from an item based on the provided list of image IDs.
     *
     * @param item The item from which images should be removed
     * @param removeImageIds List of image IDs to remove from the item
     * @throws NotFoundException if any of the images does not exist
     */
    private void removeImagesFromItem(Item item, List<Long> removeImageIds) {
        removeImageIds.forEach(imageId -> {
            Image image = validateImagesExist(imageId);
            item.removeImage(image);
            imageRepository.delete(image);
        });
    }

    /**
     * Validates that the image with the given ID exists in the database.
     *
     * @param imageId The ID of the image to validate
     * @return The Image entity if found
     * @throws NotFoundException if the image does not exist
     */
    private Image validateImagesExist(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ImageNotFound));
    }

    /**
     * Validates that the item with the given ID exists in the database.
     *
     * @param itemId The ID of the item to validate
     * @return The Item entity if found
     * @throws NotFoundException if the item does not exist
     */
    private Item validateItemsExist(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_ItemNotFound)
        );
    }

    /**
     * Updates the categories associated with the item by deleting existing associations
     * and saving the new ones based on the provided category IDs.
     *
     * @param item The Item entity to update
     * @param categoryIds List of category IDs to associate with the item
     */
    private void updateItemCategories(Item item, List<Long> categoryIds) {
        categoryItemRepository.deleteAllByItem(item);
        saveCategoryItems(categoryIds, item);
    }
}





