package yuquiz.domain.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import yuquiz.domain.user.dto.req.*;
import yuquiz.security.auth.SecurityUserDetails;

@Tag(name = "[사용자 API]", description = "사용자 관련 API")
public interface UserApi {
    @Operation(summary = "사용자 정보 불러오기", description = "로그인한 사용자의 정보를 불러오는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정보 불러오기 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "username": "test",
                                            "nickname": "테스터",
                                            "email": "test@gmail.com",
                                            "agreeEmail": false,
                                            "majorName": "컴퓨터공학과",
                                            "role": "ADMIN"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "유저 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 사용자입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> getUserInfo(@AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "사용자 정보 업데이트", description = "로그인한 사용자의 정보를 업데이트 하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "회원 정보 수정 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "password": "비밀번호는 필수 입력 값입니다.",
                                            "nickname": "닉네임은 필수 입력 값입니다.",
                                            "majorName": "학과는 필수 선택 값입니다.",
                                            "email": "이메일은 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "nickname": "닉네임은 특수문자를 제외한 2~10자리여야 합니다.",
                                            "password": "비밀번호는 8~16자 영문과 숫자를 사용하세요."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "유저 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 사용자입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> updateUserInfo(@Valid @RequestBody UserUpdateReq updateReq,
                                     @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "사용자 탈퇴", description = "로그인한 사용자가 서비스를 탈퇴하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 탈퇴 성공",
                    content = @Content(mediaType = "application/json", examples = {
                    }))
    })
    ResponseEntity<?> deleteUserInfo(@AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "사용자 비밀번호 업데이트", description = "로그인한 사용자의 비밀번호를 업데이트 하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 비밀번호 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "비밀번호 수정 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "currentPassword": "현재 비밀번호를 입력해주세요.",
                                            "newPassword": "새로운 비밀번호를 입력해주세요."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            ""newPassword": "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "유저 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "존재하지 않는 사용자입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateReq passwordReq,
                                     @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "사용자 비밀번호 확인", description = "로그인한 사용자의 비밀번호를 확인 하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 비밀번호 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Correct", value = """
                                        {
                                            "response": true
                                        }
                                    """),
                            @ExampleObject(name = "Incorrect", value = """
                                        {
                                            "response": false
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> verifyPassword(@Valid @RequestBody PasswordReq passwordReq,
                                     @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복을 확인 하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "아이디 중복 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject()
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "username": "아이디는 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "username": "아이디는 영문자와 숫자를 포함하여 6~20자여야 합니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "아이디 중복",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "message": "이미 존재하는 아이디입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> verifyUsername(@Valid @RequestBody UsernameReq usernameReq);

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복을 확인 하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 중복 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject()
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "nickname": "닉네임은 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "nickname": "닉네임은 특수문자를 제외한 2~10자리여야 합니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "message": "이미 존재하는 닉네임입니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> verifyNickname(@Valid @RequestBody NicknameReq nicknameReq);

    @Operation(summary = "인증 번호 전송", description = "회원가입 중, 인증번호를 전송하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증번호 전송 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "인증메일 보내기 성공."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "message": "이미 존재하는 이메일입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "429", description = "1분 이내에 전송한 이력 존재",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 429,
                                            "message": "1분 후 재전송 해주세요."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> sendCodeToMail(@RequestBody EmailReq emailReq);

    @Operation(summary = "인증 번호 확인", description = "회원가입 중, 인증번호를 확인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증번호 확인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "true"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "인증번호 불일치",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 400,
                                            "message": "인증번호가 일치하지 않습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404109", description = "유효시간 만료",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 410,
                                            "message": "유효시간이 지났습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> verifyCode(@RequestBody CodeVerificationReq codeReq);
}
