package com.fintrack.service;

import com.fintrack.dto.CategoryDTO;
import com.fintrack.entity.CategoryEntity;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("🧩 Testes unitários para CategoryService")
class CategoryServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(1L);
    }

    @Test
    @DisplayName("Deve salvar categoria com sucesso")
    void saveCategorySuccess() {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Alimentacao");
        dto.setType("Despesa");

        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.existsByNameAndUserId("Alimentacao", 1L)).thenReturn(false);
        when(categoryRepository.saveAndFlush(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, CategoryEntity.class));

        CategoryDTO result = categoryService.saveCategory(dto);

        assertThat(result.getName()).isEqualTo("Alimentacao");
        verify(categoryRepository).saveAndFlush(any(CategoryEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se categoria já existir")
    void saveCategoryError() {

        CategoryDTO dto = new CategoryDTO();
        dto.setName("Transporte");
        dto.setType("Despesa");

        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.existsByNameAndUserId("Transporte", 1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.saveCategory(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Já existe uma categoria com esse nome");

        verify(categoryRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve retornar categorias do usuário")
    void getCategoriesSuccess() {

        when(userService.getCurrentUser()).thenReturn(user);

        CategoryEntity category = new CategoryEntity();
        category.setName("Educacao");
        when(categoryRepository.findByUserId(1L)).thenReturn(List.of(category));

        var result = categoryService.getCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Educacao");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver categorias")
    void getCategoriesEmpty() {

        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        var result = categoryService.getCategories();

        assertThat(result).isEmpty();
    }
}