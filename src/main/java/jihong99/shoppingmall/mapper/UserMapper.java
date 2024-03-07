package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Users;

public class UserMapper {

    public static Users mapToUser(SignUpDto signUpDto) {

        return Users.builder()
                .identification(signUpDto.getIdentification())
                .password(signUpDto.getPassword())
                .name(signUpDto.getName())
                .birthDate(signUpDto.getBirthDate())
                .phoneNumber(signUpDto.getPhoneNumber())
                .build();
    }

}
