package com.fintrack.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterDTO {

    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
    private String sortField; // se eu quero filtrar por data, nome, valor...
    private String sortOrder; // asc ou desc
}
