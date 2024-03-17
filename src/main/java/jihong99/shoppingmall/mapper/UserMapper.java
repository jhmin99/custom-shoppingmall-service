package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Users;

import java.time.LocalDate;


public class UserMapper {
    public Users mapToUser(SignUpDto signUpDto) {
        return Users.builder()
                .identification(signUpDto.getIdentification())
                .password(signUpDto.getPassword())
                .name(signUpDto.getName())
                .birthDate(LocalDate.parse(signUpDto.getBirthDate()))
                .phoneNumber(signUpDto.getPhoneNumber())
                .build();
    }

}
