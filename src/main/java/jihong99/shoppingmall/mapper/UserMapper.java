package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.User;

public class UserMapper {

    public static User mapToUser(SignUpDto signUpDto) {

        return User.builder()
                .identification(signUpDto.getIdentification())
                .password(signUpDto.getPassword())
                .name(signUpDto.getName())
                .birthDate(signUpDto.getBirthDate())
                .phoneNumber(signUpDto.getPhoneNumber())
                .build();
    }

}
