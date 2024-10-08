package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.item.ItemRequestDto;
import jihong99.shoppingmall.dto.request.item.PatchItemRequestDto;
import jihong99.shoppingmall.dto.request.item.UpdateStockRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IItemService {
    void createItem(ItemRequestDto itemRequestDto, List<MultipartFile> images);
    void patchItem(Long id, PatchItemRequestDto patchItemRequestDto, List<MultipartFile> addImages, List<Long> removeImageIds);
    void markItemAsInvalid(Long id);
    void markItemAsValid(Long id);
    void updateItemStock(Long id, UpdateStockRequestDto updateStockRequestDto);
}
