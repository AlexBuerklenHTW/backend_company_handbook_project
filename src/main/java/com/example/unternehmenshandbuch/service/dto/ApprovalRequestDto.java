package com.example.unternehmenshandbuch.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApprovalRequestDto {
	private String status;
	private Integer version;
	private String editedBy;
}
