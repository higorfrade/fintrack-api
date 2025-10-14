package com.fintrack.service;

import com.fintrack.dto.CategoryDTO;
import com.fintrack.entity.CategoryEntity;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserService userService;
    private final CategoryRepository categoryRepository;

    // Salva a categoria
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        UserEntity user = userService.getCurrentUser();

        // Verifica se já existe uma categoria com esse nome
        if (categoryRepository.existsByNameAndUserId(categoryDTO.getName(), user.getId())) {
            throw new RuntimeException("Já existe uma categoria com esse nome");
        }

        CategoryEntity newCategory = toEntity(categoryDTO, user);
        newCategory = categoryRepository.saveAndFlush(newCategory);

        return toDTO(newCategory);
    }

    // Retorna categorias do usuário
    public List<CategoryDTO> getCategories() {
        UserEntity user = userService.getCurrentUser();
        List<CategoryEntity> categories = categoryRepository.findByUserId(user.getId());

        return categories.stream().map(this::toDTO).toList();
    }

    // Retorna categorias do usuário por Tipo
    public List<CategoryDTO> getCategoriesByType(String type) {
        UserEntity user = userService.getCurrentUser();
        List<CategoryEntity> categories = categoryRepository.findByTypeAndUserId(type, user.getId());

        return categories.stream().map(this::toDTO).toList();
    }

    // Atualizar categoria
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
        UserEntity user = userService.getCurrentUser();
        CategoryEntity category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada ou inacessivel"));
        category.setName(dto.getName());
        category.setType(dto.getType());
        category.setIcon(dto.getIcon());
        category = categoryRepository.saveAndFlush(category);

        return toDTO(category);
    }

    private CategoryEntity toEntity(CategoryDTO categoryDTO, UserEntity user) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .type(categoryDTO.getType())
                .user(user)
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity categoryEntity) {
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .userId(categoryEntity.getUser() != null ? categoryEntity.getUser().getId() : null )
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .type(categoryEntity.getType())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .build();
    }
}
