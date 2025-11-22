package com.project.ecommerce.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    @Schema(description = "Category ID for a particular category", example = "101")
    private Long categoryId;
    @Schema(description = "Category name for category you wish to create", example = "Iphone 16")
    private String categoryName;
}
