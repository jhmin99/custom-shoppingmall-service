package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Users;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    @Test
    public void mapToUserSuccess(){
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-12-30", "01012341234");
        // when
        UserMapper userMapper = new UserMapper();
        Users mappedUser = userMapper.mapToUser(signUpDto);
        // then
        Assertions.assertThat(mappedUser.getIdentification()).isEqualTo("abcd123");
        Assertions.assertThat(mappedUser.getPassword()).isEqualTo("abcd123!@#");
        Assertions.assertThat(mappedUser.getName()).isEqualTo("민지홍");

        Assertions.assertThat(mappedUser.getBirthDate().getYear()).isEqualTo(1999);
        Assertions.assertThat(mappedUser.getBirthDate().getMonthValue()).isEqualTo(12);
        Assertions.assertThat(mappedUser.getBirthDate().getDayOfMonth()).isEqualTo(30);

        Assertions.assertThat(mappedUser.getPhoneNumber()).isEqualTo("01012341234");
    }


    @Test
    public void mapToUserBirthDateParseError(){
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#",
                "abcd123!@#", "민지홍", "1999-15-30", "01012341234");
        // when & then
        assertThrows(DateTimeParseException.class, () -> {
            UserMapper userMapper = new UserMapper();
            userMapper.mapToUser(signUpDto);
        });
    }

}