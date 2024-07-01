package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.dto.ItemResponseDto;
import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static jihong99.shoppingmall.constants.Constants.MESSAGE_404_CategoryNotFound;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {

    private final IItemService iitemService;

    @PostMapping("/admin/item")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        try {
            ItemResponseDto createdItem = iitemService.createItem(itemRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDto(Constants.STATUS_404, MESSAGE_404_CategoryNotFound));
        }

    }
}
