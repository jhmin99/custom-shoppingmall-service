package jihong99.shoppingmall.service;

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
import org.springframework.transaction.annotation.Transactional;
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
     * @param images         A list of images associated with the item
     * @throws NotFoundException        if any category ID provided does not exist
     * @throws ImageUploadException     if any image fails to upload
     * @throws IllegalArgumentException if the storage type for file uploads is invalid
     */
    @Transactional
    @Override
    public void createItem(ItemRequestDto itemRequestDto, List<MultipartFile> images) {
        checkCategoriesExistence(itemRequestDto.getCategoryIds());
        List<Image> imageEntities = uploadAndPersistImages(images);
        Item item = createAndSaveItem(itemRequestDto, imageEntities);
        associateCategoriesWithItem(itemRequestDto.getCategoryIds(), item);
    }

    /**
     * Updates an existing item with the provided details, images to add, and images to remove.
     *
     * @param itemId              The ID of the item to update
     * @param patchItemRequestDto DTO containing item details to update such as name, price, keyword, and categories
     * @param addImages           List of images to add to the item
     * @param removeImageIds      List of image IDs to remove from the item
     * @throws NotFoundException        if the item, category, or image does not exist
     * @throws ImageUploadException     if any image fails to upload
     * @throws IllegalArgumentException if the storage type for file uploads is invalid
     */
    @Override
    @Transactional
    public void patchItem(Long itemId, PatchItemRequestDto patchItemRequestDto, List<MultipartFile> addImages, List<Long> removeImageIds) {
        Item item = findItemOrThrow(itemId);
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
            updateItemCategoryAssociations(item, patchItemRequestDto.getCategoryIds());
        }
        if (removeImageIds != null && !removeImageIds.isEmpty()) {
            deleteImagesFromItem(item, removeImageIds);
        }
        if (addImages != null && !addImages.isEmpty()) {
            List<Image> imageEntities = uploadAndPersistImages(addImages);
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
        Item item = findItemOrThrow(id);
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
        Item item = findItemOrThrow(id);
        item.validateItem();
        itemRepository.save(item);

    }

    /**
     * Updates the stock levels of an item.
     *
     * @param id                    The ID of the item whose stock is to be updated
     * @param updateStockRequestDto DTO containing the new stock level
     */
    @Override
    @Transactional
    public void updateItemStock(Long id, UpdateStockRequestDto updateStockRequestDto) {
        Item item = findItemOrThrow(id);
        item.updateStock(updateStockRequestDto.getStock());
        itemRepository.save(item);
    }



    private void deleteImagesFromItem(Item item, List<Long> removeImageIds) {
        removeImageIds.forEach(imageId -> {
            Image image = findImageOrThrow(imageId);
            item.removeImage(image);
            imageRepository.delete(image);
        });
    }

    private Image findImageOrThrow(Long imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ImageNotFound));
    }

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_ItemNotFound)
        );
    }

    private void updateItemCategoryAssociations(Item item, List<Long> categoryIds) {
        categoryItemRepository.deleteAllByItem(item);
        associateCategoriesWithItem(categoryIds, item);
    }



    private void checkCategoriesExistence(List<Long> categoryIds) {
        categoryIds.forEach(categoryId -> {
            if (!categoryRepository.existsById(categoryId)) {
                throw new NotFoundException(String.format(MESSAGE_404_CategoryNotFound + " " + categoryId));
            }
        });
    }


    private static CategoryItem buildCategoryItem(Item item, Category category) {
        return CategoryItem.builder()
                .category(category)
                .item(item).build();
    }


    private void associateCategoriesWithItem(List<Long> categoryIds, Item item) {
        for (Long categoryId : categoryIds) {
            Category category = findCategoryOrThrow(categoryId);
            CategoryItem categoryItem = buildCategoryItem(item, category);
            categoryItemRepository.save(categoryItem);
        }
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_CategoryNotFound));
    }


    private Item createAndSaveItem(ItemRequestDto itemRequestDto, List<Image> imageEntities) {
        Item item = buildItemEntity(itemRequestDto, imageEntities);
        return itemRepository.save(item);
    }

    private static Item buildItemEntity(ItemRequestDto itemRequestDto, List<Image> imageEntities) {
        return Item.createItem(
                itemRequestDto.getName(),
                itemRequestDto.getPrice(),
                itemRequestDto.getStock(),
                itemRequestDto.getKeyword(),
                imageEntities);

    }


    private List<Image> uploadAndPersistImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        return images.stream()
                .map(this::uploadAndPersistSingleImage)
                .collect(Collectors.toList());
    }


    private Image uploadAndPersistSingleImage(MultipartFile image) {
        try {
            String imageUrl = fileStorageService.uploadFile(image.getOriginalFilename(), image.getInputStream(), image.getContentType());
            Image savedImage = buildImageEntity(image, imageUrl);
            return imageRepository.save(savedImage);
        } catch (IOException e) {
            throw new ImageUploadException(MESSAGE_500_ImageUploadFailed);
        }
    }

    private static Image buildImageEntity(MultipartFile image, String imageUrl) {
        return Image.builder()
                .name(image.getOriginalFilename())
                .url(imageUrl)
                .type(image.getContentType())
                .size(image.getSize())
                .build();
    }


}





