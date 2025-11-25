package com.team6.backend.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team6.backend.api.dto.response.AuthResponseDTO;
import com.team6.backend.common.dto.DataResponse;
import com.team6.backend.domain.user.application.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class UserController {

	private final UserService userService;

	@Operation(summary = "카카오 로그인 API")
	@GetMapping("/kakao")
	public ResponseEntity<DataResponse<AuthResponseDTO.LoginResponse>> kakaoLogin(
		@RequestParam("code") String code
	) {
		return ResponseEntity.ok(
			DataResponse.from(
				userService.kakaoLogin(code)
			)
		);
	}
}
