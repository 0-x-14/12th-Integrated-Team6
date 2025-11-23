package com.team6.backend.api.dto.response;

public record UserLocationResponseDTO(
	Long locationId, String name, Boolean pinned
) {
}
