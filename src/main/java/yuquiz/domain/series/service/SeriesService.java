package yuquiz.domain.series.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuquiz.common.exception.CustomException;
import yuquiz.domain.series.dto.SeriesReq;
import yuquiz.domain.series.dto.SeriesRes;
import yuquiz.domain.series.dto.SeriesSortType;
import yuquiz.domain.series.dto.SeriesSummaryRes;
import yuquiz.domain.series.entity.Series;
import yuquiz.domain.series.exception.SeriesExceptionCode;
import yuquiz.domain.series.repository.SeriesRepository;
import yuquiz.domain.study.entity.Study;
import yuquiz.domain.study.exception.StudyExceptionCode;
import yuquiz.domain.study.repository.StudyRepository;
import yuquiz.domain.studyUser.repository.StudyUserRepository;
import yuquiz.domain.user.entity.User;
import yuquiz.domain.user.exception.UserExceptionCode;
import yuquiz.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final StudyRepository studyRepository;
    private final StudyUserRepository studyUserRepository;
    private final UserRepository userRepository;

    private final Integer SERIES_PER_PAGE = 20;

    @Transactional(readOnly = true)
    public void createSeries(SeriesReq seriesReq, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserExceptionCode.INVALID_USERID));

        Study study = null;
        if (seriesReq.studyId() != null) {
            study = studyRepository.findById(seriesReq.studyId())
                    .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));

            if (!validateMember(study.getId(), userId)) {
                throw new CustomException(SeriesExceptionCode.UNAUTHORIZED_ACTION);
            }
        }

        seriesRepository.save(seriesReq.toEntity(user, study));
    }

    @Transactional(readOnly = true)
    public SeriesRes findSeriesById(Long seriesId, Long userId) {

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new CustomException(SeriesExceptionCode.INVALID_ID));

        boolean isAuthorized = Optional.ofNullable(series.getStudy())
                .map(study -> validateMember(study.getId(), userId))
                .orElse(true);

        if (!isAuthorized) {
            throw new CustomException(SeriesExceptionCode.UNAUTHORIZED_ACTION);
        }

        return SeriesRes.fromEntity(series);
    }

    @Transactional
    public void deleteSeriesById(Long seriesId, Long userId) {

        if (!validateCreator(seriesId, userId)) {
            throw new CustomException(SeriesExceptionCode.UNAUTHORIZED_ACTION);
        }

        seriesRepository.deleteById(seriesId);
    }

    @Transactional
    public void updateSeriesById(Long seriesId, SeriesReq seriesReq, Long userId) {

        if (!validateCreator(seriesId, userId)) {
            throw new CustomException(SeriesExceptionCode.UNAUTHORIZED_ACTION);
        }

        Study study = null;
        if (seriesReq.studyId() != null) {
            study = studyRepository.findById(seriesReq.studyId())
                    .orElseThrow(() -> new CustomException(StudyExceptionCode.INVALID_ID));
        }

        Series series = seriesRepository.findById(seriesId)
                .orElseThrow(() -> new CustomException(SeriesExceptionCode.INVALID_ID));

        series.update(seriesReq, study);
    }

    @Transactional(readOnly = true)
    public Page<SeriesSummaryRes> getSeriesSummary(String keyword, SeriesSortType sort, Integer page) {

        Pageable pageable = PageRequest.of(page, SERIES_PER_PAGE, sort.getSort());

        Page<Series> series = seriesRepository.findByKeywordAndStudyIsNull(keyword, pageable);

        return series.map(SeriesSummaryRes::fromEntity);
    }

    private boolean validateCreator(Long seriesId, Long userId) {
        return seriesRepository.findCreatorIdById(seriesId)
                .map(creatorId -> creatorId.equals(userId))
                .orElse(false);
    }

    private boolean validateMember(Long studyId, Long userId) {
        return studyUserRepository.existsByStudy_IdAndUser_Id(studyId, userId);
    }
}
