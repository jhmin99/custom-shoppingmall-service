package jihong99.shoppingmall.dto;

import jihong99.shoppingmall.entity.DeliveryAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

import static jihong99.shoppingmall.constants.UserConstants.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageResponseDto {
    private String statusCode;
    private String statusMessage;
    private String identification;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private Set<DeliveryAddress> deliveryAddresses;


    public static MyPageResponseDto success(String identification, String name, LocalDate birthDate, String phoneNumber, Set<DeliveryAddress> deliveryAddresses) {
        return new MyPageResponseDto(STATUS_200, MESSAGE_200_fetchSuccess, identification, name, birthDate, phoneNumber, deliveryAddresses);
    }

    public static MyPageResponseDto error(String statusCode, String statusMessage) {
        return new MyPageResponseDto(statusCode, statusMessage, null, null, null, null, null);
    }
}
