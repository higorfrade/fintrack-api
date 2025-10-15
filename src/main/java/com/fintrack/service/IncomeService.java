package com.fintrack.service;

import com.fintrack.dto.IncomeDTO;
import com.fintrack.entity.CategoryEntity;
import com.fintrack.entity.IncomeEntity;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final UserService userService;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        UserEntity user = userService.getCurrentUser();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        IncomeEntity newIncome = toEntity(incomeDTO, user, category);
        newIncome = incomeRepository.saveAndFlush(newIncome);

        return toDTO(newIncome);
    }

    public List<IncomeDTO> getCurrentMonthIncomes() {
        UserEntity user = userService.getCurrentUser();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        List<IncomeEntity> list = incomeRepository.findByUserIdAndDateBetween(user.getId(), startDate, endDate);

        return list.stream().map(this::toDTO).toList();
    }

    public List<IncomeDTO> getLatest5Incomes() {
        UserEntity user = userService.getCurrentUser();
        List<IncomeEntity> list = incomeRepository.findTop5ByUserIdOrderByDateDesc(user.getId());

        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalIncomes() {
        UserEntity user = userService.getCurrentUser();
        BigDecimal total = incomeRepository.findTotalByUserId(user.getId());

        return total != null ? total : BigDecimal.ZERO;
    }

    public void deleteIncome(Long incomeId) {
        UserEntity user = userService.getCurrentUser();

        IncomeEntity income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Receita não encontrada"));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Exclusão de receita não permitida");
        }

        incomeRepository.delete(income);
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        UserEntity user = userService.getCurrentUser();
        List<IncomeEntity> list = incomeRepository.findByUserIdAndDateBetweenAndNameContainingIgnoreCase(user.getId(), startDate, endDate, keyword, sort);

        return list.stream().map(this::toDTO).toList();
    }

    private IncomeEntity toEntity(IncomeDTO incomeDTO, UserEntity user, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .amount(incomeDTO.getAmount())
                .icon(incomeDTO.getIcon())
                .date(incomeDTO.getDate())
                .user(user)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .amount(incomeEntity.getAmount())
                .icon(incomeEntity.getIcon())
                .date(incomeEntity.getDate())
                .categoryId(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getId() : null)
                .categoryName(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getName() : "N/A")
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}
