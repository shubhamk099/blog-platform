package com.codeplay.blogapp.services;

import java.util.List;
import java.util.UUID;

import com.codeplay.blogapp.domain.entities.Category;

public interface CategoryService {

    List<Category> listCategories();

    Category createCategory(Category category);

    void deleteCategory(UUID id);
}
