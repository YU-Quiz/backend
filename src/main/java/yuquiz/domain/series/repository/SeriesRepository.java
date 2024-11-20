package yuquiz.domain.series.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import yuquiz.domain.series.entity.Series;

import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {

    @Query("select s.creator.id from Series s where s.id = :id")
    Optional<Long> findCreatorIdById(@Param("id") Long id);

    @Query("select s from Series s " +
            "where s.study is null " +
            "and (:keyword is null or s.name like %:keyword%)")
    Page<Series> findByKeywordAndStudyIsNull(@Param("keyword") String keyword, Pageable pageable);

    @Query("select s.study.id from Series s where s.id = :id")
    Optional<Long> findStudyIdById(@Param("id") Long id);

    @Query("select s from Series s " +
            "where s.study.id = :study " +
            "and (:keyword is null or s.name like %:keyword%)")
    Page<Series> findByStudyAndKeywordAndStudyIsNull(@Param("keyword") String keyword, @Param("study") Long studyId, Pageable pageable);
}
