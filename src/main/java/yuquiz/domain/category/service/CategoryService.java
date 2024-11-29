package yuquiz.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuquiz.domain.category.dto.CategorySummaryRes;
import yuquiz.domain.category.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategorySummaryRes> getCategories() {

        return categoryRepository.findAll().stream()
                .filter(category -> category.getId() != 3)
                .map(CategorySummaryRes::fromEntity)
                .collect(Collectors.toList());
    }
}
