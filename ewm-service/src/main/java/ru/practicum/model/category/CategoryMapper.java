package ru.practicum.model.category;

import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName());
    }

    public Category toCategory(CategoryDto categoryDto) {
        return new Category(
                categoryDto.getId(),
                categoryDto.getName());
    }

    public Category toCategory(NewCategoryDto categoryDto) {
        Category request = new Category();
        request.setName(categoryDto.getName());
        return request;
    }
}
