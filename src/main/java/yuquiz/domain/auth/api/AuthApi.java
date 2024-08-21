package yuquiz.domain.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import yuquiz.domain.auth.dto.OAuthCodeDto;
import yuquiz.domain.auth.dto.SignInReq;

import static yuquiz.common.utils.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static yuquiz.common.utils.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthApi {
    @Operation(summary = "일반 로그인", description = "서비스를 이용하기 위해 로그인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "nickname": "테스터",
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiO0NiIsInJvbGUiOiJPV05FUiIsImNhdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "사용자 없음 및 비밀번호 틀림",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 401,
                                            "message": "아이디 또는 비밀번호가 유효하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> signIn(@Valid @RequestBody SignInReq signInReq);

    @Operation(summary = "accessToken 재발급", description = "서버 인증을 위한 accessToken 재발급을 위한 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOisInJvbGUiOiJPV05FUiIsImNhdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA"
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "RefreshToken 존재 x ",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "RefreshToken이 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> reIssueToken(@CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken);

    @Operation(summary = "소셜 로그인(카카오)", description = "서비스를 이용하기 위해 로그인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "FirstLogin", value = """
                                        {
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIisInJvbGUiOiJPV05FUiIsImNhdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA",
                                            "isRegistered": true
                                        }
                                    """),
                            @ExampleObject(name = "ReturningLogin", value = """
                                        {
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXNbGUiOiJPV05FUiIsImNhdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA",
                                            "isRegistered": false
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> kakaoSignIn(@RequestBody OAuthCodeDto oauthCodeDto);

    @Operation(summary = "소셜 로그인(네이버)", description = "서비스를 이용하기 위해 로그인하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "FirstLogin", value = """
                                        {
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0NiIsbGUiOiJPV05FUiIsImNhdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA",
                                            "isRegistered": true
                                        }
                                    """),
                            @ExampleObject(name = "ReturningLogin", value = """
                                        {
                                            "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0NiIsInJvbGUiOiJPV05FUihdGVnb3J5IjoiYWNjZXNzIiwidXNlcklkIjo2LCJpYXQiOjE3MjI2Njc1MzYsImV4cCI6MTcyMjY2OTMzNn0.9eY_1aSfKLfDhKN5X4f85N2hv_I65QOPFtq_2YXEhoA",
                                            "isRegistered": false
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> naverSignIn(@RequestBody OAuthCodeDto oauthCodeDto);

    @Operation(summary = "로그아웃", description = "사용자가 로그아웃하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "response": "로그아웃 되었습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "refreshToken or accessToken 존재하지 않음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "message": "Token이 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> signOut(@RequestHeader(value = ACCESS_HEADER_VALUE, required = false) String accessToken,
                              @CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken,
                              HttpServletResponse response);

}