package yuquiz.domain.major.dto;

import yuquiz.domain.major.entity.Major;

public record MajorRes(
        Long id,
        String name
) {
    public static MajorRes fromEntity(Major major) {
        return new MajorRes(major.getId(), major.getMajorName());
    }
}
