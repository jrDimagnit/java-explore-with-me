package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundCategoryException;
import ru.practicum.model.category.Category;
import ru.practicum.model.category.CategoryDto;
import ru.practicum.model.category.CategoryMapper;
import ru.practicum.model.category.NewCategoryDto;
import ru.practicum.repository.CategoryRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CategoryService {
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;


    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int fromNum, int size) {
        int from = fromNum >= 0 ? fromNum / size : 0;
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Category> categories = categoryRepository.findAll(page);

        return categories.stream().map(categoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId, HttpServletRequest request) {
        Category category = checkCategory(catId, request);
        return categoryMapper.toCategoryDto(category);
    }

    public Category checkCategory(Long catId, HttpServletRequest request) {
        Optional<Category> categoryOptional = categoryRepository.findById(catId);

        if (categoryOptional.isPresent())
            return categoryOptional.get();
        else
            throw new NotFoundCategoryException(catId, request);
    }

    public CategoryDto addNewCategory(NewCategoryDto newCategory, HttpServletRequest request) {
        checkCategoryName(newCategory.getName(), request);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategory)));
    }

    private void checkCategoryName(String catName, HttpServletRequest request) {
        if (catName == null)
            throw new BadRequestException(request.getParameterMap().toString());
        if (getCategoryNames().contains(catName))
            throw new ConflictException(String.format("Такая категория %s уже существует! Request path = %s",
                    catName, request.getRequestURI()));
    }

    public CategoryDto modifyCategory(CategoryDto categoryDto, HttpServletRequest request) {
        if (categoryDto.getId() <= 0)
            throw new BadRequestException(request.getParameterMap().toString());
        checkCategoryName(categoryDto.getName(), request);

        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
    }

    public Set<String> getCategoryNames() {
        return categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toSet());
    }

    public void deleteCategory(long catId) {
        categoryRepository.deleteById(catId);
    }
}
