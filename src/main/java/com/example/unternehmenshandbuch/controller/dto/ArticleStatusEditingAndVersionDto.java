package com.example.unternehmenshandbuch.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleStatusEditingAndVersionDto {
    private Integer version;
    private String editedBy;
}
