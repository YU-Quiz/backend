package yuquiz.domain.post.dto;

import org.springframework.data.domain.Sort;

public enum PostSortType {
    TITL_DESC("title", Sort.Direction.DESC),
    TITL_ASC("title", Sort.Direction.ASC),
    DATE_DESC("createdAt", Sort.Direction.DESC),
    DATE_ASC("createdAt", Sort.Direction.ASC);

    private String type;
    private Sort.Direction direction;

    PostSortType(String type, Sort.Direction direction) {
        this.type = type;
        this.direction = direction;
    }

    public Sort getSort() {
        return Sort.by(direction, type);
    }
}