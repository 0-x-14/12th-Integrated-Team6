package com.team6.backend.api.dto.response;

public record AuthResponseDTO (
	LoginResponse loginResponse
){
	public record LoginResponse(
		Long userId, AuthTokens token
	) {}

	public record AuthTokens(
		String accessToken, String refreshToken
	) {}
}