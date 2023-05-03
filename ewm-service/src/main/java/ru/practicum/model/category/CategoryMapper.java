package ru.practicum.model.category;

import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {

    public Category toCategory(NewCategoryDto categoryDto) {
        Category request = new Category();
        request.setName(categoryDto.getName());
        return request;
    }
}
