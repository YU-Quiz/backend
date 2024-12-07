package yuquiz.domain.post.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import yuquiz.domain.post.dto.PostReq;
import yuquiz.domain.post.dto.PostSortType;
import yuquiz.security.auth.SecurityUserDetails;

@Tag(name = "[게시글 API]", description = "게시글 관련 API")
public interface PostApi {

    @Operation(summary = "게시글 생성", description = "게시글을 생성하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "퀴즈 생성 성공."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "title": "제목은 필수 입력입니다.",
                                        "content": "내용은 필수 입력 입니다.",
                                        "categoryId": "카테고리는 필수 입력입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> createPost(@Valid @RequestBody PostReq postReq,
                                 @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "게시글 조회", description = "게시글 ID로 게시글을 조회하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "comments": [
                                             {
                                                 "id": 1,
                                                 "content": "123",
                                                 "writerName": "테스터",
                                                 "createdAt": "2024-09-05T23:30:58",
                                                 "modified": true,
                                                 "isWriter": true
                                             },
                                             {
                                                 "id": 8,
                                                 "content": "123",
                                                 "writerName": "테스터",
                                                 "createdAt": "2024-09-05T23:30:58",
                                                 "modified": true,
                                                 "isWriter": true
                                             },
                                             {
                                                 "id": 13,
                                                 "content": "123",
                                                 "writerName": "테스터111",
                                                 "createdAt": "2024-09-05T23:30:58",
                                                 "modified": true,
                                                 "isWriter": false
                                             }
                                         ],
                                         "post": {
                                             "title": "새로운 타이틀",
                                             "content": "새로운 컨텐츠",
                                             "category": "자유게시판",
                                             "nickname": "테스터111",
                                             "likeCount": 0,
                                             "viewCount": 1,
                                             "createdAt": "2024-08-20T14:57:51.031651",
                                             "modified": true,
                                             "isLiked": true,
                                             "isWriter": false,
                                             "isWriter": true
                                         }
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시글",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 404,
                                        "message": "존재하지 않는 게시글 입니다."
                                    }
                                    """)
                    })),

    })
    ResponseEntity<?> getPostById(@PathVariable(value = "postId") Long postId, @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회하는 API, 키워드, 카테고리 옵션 설정 가능")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "totalPages": 1,
                                        "totalElements": 5,
                                        "first": true,
                                        "last": true,
                                        "size": 20,
                                        "content": [
                                            {
                                                "postId": 12,
                                                "postTitle": "qwe12",
                                                "nickname": "test1111",
                                                "categoryName": "공지게시판",
                                                "createdAt": "2024-08-22T18:28:30.319814",
                                                "likeCount": 1,
                                                "viewCount": 0
                                            },
                                            {
                                                "postId": 9,
                                                "postTitle": "2제목",
                                                "nickname": "테스터",
                                                "categoryName": "공지게시판",
                                                "createdAt": "2024-08-20T15:33:51.620851",
                                                "likeCount": 0,
                                                "viewCount": 0
                                            },
                                            {
                                                "postId": 8,
                                                "postTitle": "새로운 타이틀",
                                                "nickname": "테스터",
                                                "categoryName": "자유게시판",
                                                "createdAt": "2024-08-20T15:33:46.902076",
                                                "likeCount": 1,
                                                "viewCount": 0
                                            },
                                            {
                                                "postId": 6,
                                                "postTitle": "새로운 타이틀",
                                                "nickname": "테스터",
                                                "categoryName": "자유게시판",
                                                "createdAt": "2024-08-20T15:33:34.426304",
                                                "likeCount": 0,
                                                "viewCount": 0
                                            },
                                            {
                                                "postId": 4,
                                                "postTitle": "새로운 타이틀",
                                                "nickname": "테스터111",
                                                "categoryName": "자유게시판",
                                                "createdAt": "2024-08-20T14:57:51.031651",
                                                "likeCount": 0,
                                                "viewCount": 0
                                            }
                                        ],
                                        "number": 0,
                                        "sort": {
                                            "empty": false,
                                            "unsorted": false,
                                            "sorted": true
                                        },
                                        "pageable": {
                                            "pageNumber": 0,
                                            "pageSize": 20,
                                            "sort": {
                                                "empty": false,
                                                "unsorted": false,
                                                "sorted": true
                                            },
                                            "offset": 0,
                                            "unpaged": false,
                                            "paged": true
                                        },
                                        "numberOfElements": 5,
                                        "empty": false
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getPosts(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryName", required = false) Long categoryId,
                               @RequestParam(value = "sort") PostSortType sort,
                               @RequestParam(value = "page") @Min(0) Integer page);

    @Operation(summary = "내가 작성한 게시글 목록 조회", description = "내가 작성한 게시글 목록을 조회하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "totalPages": 1,
                                         "totalElements": 4,
                                         "first": true,
                                         "last": true,
                                         "size": 20,
                                         "content": [
                                             {
                                                 "postId": 13,
                                                 "postTitle": "새로운 타이d틀",
                                                 "nickname": "테스터",
                                                 "categoryName": "자유게시판",
                                                 "createdAt": "2024-09-12T16:36:17.781965",
                                                 "likeCount": 0,
                                                 "viewCount": 4
                                             },
                                             {
                                                 "postId": 9,
                                                 "postTitle": "2제목",
                                                 "nickname": "테스터",
                                                 "categoryName": "공지게시판",
                                                 "createdAt": "2024-08-20T15:33:51.620851",
                                                 "likeCount": 1,
                                                 "viewCount": 0
                                             },
                                             {
                                                 "postId": 8,
                                                 "postTitle": "새로운 타이틀",
                                                 "nickname": "테스터",
                                                 "categoryName": "자유게시판",
                                                 "createdAt": "2024-08-20T15:33:46.902076",
                                                 "likeCount": 2,
                                                 "viewCount": 2
                                             },
                                             {
                                                 "postId": 6,
                                                 "postTitle": "새로운 타이d틀",
                                                 "nickname": "테스터",
                                                 "categoryName": "자유게시판",
                                                 "createdAt": "2024-08-20T15:33:34.426304",
                                                 "likeCount": 0,
                                                 "viewCount": 0
                                             }
                                         ],
                                         "number": 0,
                                         "sort": {
                                             "empty": false,
                                             "sorted": true,
                                             "unsorted": false
                                         },
                                         "numberOfElements": 4,
                                         "pageable": {
                                             "pageNumber": 0,
                                             "pageSize": 20,
                                             "sort": {
                                                 "empty": false,
                                                 "sorted": true,
                                                 "unsorted": false
                                             },
                                             "offset": 0,
                                             "paged": true,
                                             "unpaged": false
                                         },
                                         "empty": false
                                     }
                                    """)
                    }))
    })
    ResponseEntity<?> getPostsByWriter(@AuthenticationPrincipal SecurityUserDetails userDetails,
                                       @RequestParam(value = "sort", defaultValue = "DATE_DESC") PostSortType sort,
                                       @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page);

    @Operation(summary = "게시글 수정", description = "게시글을 수정하는 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "response": "게시글 수정 성공."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "작성자 불일치 및 없는 게시글",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "title": "제목은 필수 입력입니다.",
                                        "content": "내용은 필수 입력 입니다.",
                                        "categoryId": "카테고리는 필수 입력입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> updatePost(@PathVariable(value = "postId") Long postId,
                                 @Valid @RequestBody PostReq postReq,
                                 @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "게시글 삭제", description = "게시글 삭제 관련 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성자 불일치 및 없는 게시글",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": 403,
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> deletePost(@PathVariable(value = "postId") Long postId,
                                 @AuthenticationPrincipal SecurityUserDetails userDetails);

}
