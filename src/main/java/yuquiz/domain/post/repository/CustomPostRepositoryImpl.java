package yuquiz.domain.post.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import yuquiz.domain.post.dto.PostSortType;
import yuquiz.domain.post.entity.Post;

import java.util.List;

import static yuquiz.domain.post.entity.QPost.post;

public class CustomPostRepositoryImpl implements CustomPostRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomPostRepositoryImpl(EntityManager entityManager){
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Post> getPosts(String keyword, Long categoryId, Pageable pageable, PostSortType sort) {
        List<Post> posts = jpaQueryFactory
                .select(post)
                .from(post)
                .where(wordContain(keyword), categoryEqual(categoryId))
                .orderBy(sort.getOrder())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(wordContain(keyword), categoryEqual(categoryId))
                .fetchOne();

        return new PageImpl<>(posts, pageable, total);
    }

    private BooleanExpression wordContain(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        return Expressions.booleanTemplate(
                "function('match', {0}, {1}, {2}) > 0",
                post.title,
                post.content,
                keyword
        );
    }

    private BooleanExpression categoryEqual(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return post.category.id.eq(categoryId);
    }
}