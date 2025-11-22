package com.team6.backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team6.backend.api.dto.request.SaveLocationRequestDTO;
import com.team6.backend.api.dto.response.DefaultIdResponse;
import com.team6.backend.common.dto.DataResponse;
import com.team6.backend.domain.userLocation.application.UserLocationService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/locations")
public class UserLocationController {
	private final UserLocationService userLocationService;

	@Operation(summary = "장소 저장 API")
	@PostMapping("/save")
	public ResponseEntity<DataResponse<DefaultIdResponse>> saveLocation(
		@RequestParam Long userId, // 로그인 구현 전이므로 임시로 userId 입력받아서 처리
		@RequestBody SaveLocationRequestDTO saveLocationRequestDTO
	) {
		return ResponseEntity.ok(
			DataResponse.created(
				DefaultIdResponse.of(userLocationService.saveLocation(userId, saveLocationRequestDTO))
			)
		);
	}
}
