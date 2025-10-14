package com.fintrack.repository;

import com.fintrack.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    // SELECT * FROM categories WHERE user_id = ?
    List<CategoryEntity> findByUserId(Long userId);

    // SELECT * FROM categories WHERE id = ? and user_id = ?
    Optional<CategoryEntity> findByIdAndUserId(Long id, Long userId);

    // SELECT * FROM categories WHERE type = ? and user_id = ?
    List<CategoryEntity> findByTypeAndUserId(String type, Long userId);

    // SELECT * FROM categories WHERE name = ? and user_id = ? (if exists ? true : false)
    Boolean existsByNameAndUserId(String name, Long userId);
}
