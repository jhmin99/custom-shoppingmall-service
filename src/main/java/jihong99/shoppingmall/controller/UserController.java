package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.constants.UserConstants;
import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final IUserService iuserService;

    /**
     * 아이디 중복 확인
     * POST 요청을 통해 중복된 아이디인지 확인합니다.
     *
     * @param signUpDto 회원가입에 필요한 정보를 포함한 DTO 객체
     * @return ResponseEntity<ResponseDto> 아이디 중복 결과를 포함한 응답 객체
     *
     * 응답코드
     * - 200 : 아이디 사용가능
     * - 400 : 중복된 아이디 존재
     * - 500 : 내부 서버 에러
     */
    @PostMapping("/users/check-id")
    public ResponseEntity<ResponseDto> checkDuplicateIdentification(@RequestBody SignUpDto signUpDto){
        try{
            if(iuserService.isIdentificationVerified(signUpDto.getIdentification())) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseDto(UserConstants.STATUS_200, UserConstants.MESSAGE_200_verifiedId));
            }
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(UserConstants.STATUS_500, UserConstants.MESSAGE_500));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_duplicatedId));
    }

    /**
     * 회원가입
     * POST 요청을 통해 새로운 사용자를 등록합니다.
     *
     * @param signUpDto 회원가입에 필요한 정보를 포함한 DTO 객체
     * @return ResponseEntity<ResponseDto> 회원가입 결과를 포함한 응답 객체
     *
     *  응답코드
     * - 201 : 정상적으로 회원 생성
     * - 400 : 비밀번호 불일치
     * - 500 : 내부 서버 에러
     */
    @PostMapping("/users")
    public ResponseEntity<ResponseDto> signUp(@Valid @RequestBody SignUpDto signUpDto){

        try{
            iuserService.signUpAccount(signUpDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDto(UserConstants.STATUS_201, UserConstants.MESSAGE_201_createUser));
        }catch(PasswordMismatchException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_MissMatchPw));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto(UserConstants.STATUS_500, UserConstants.MESSAGE_500));
        }

    }


}
