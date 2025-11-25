package com.team6.backend.domain.user.application;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.backend.api.dto.response.AuthResponseDTO;
import com.team6.backend.common.jwt.JwtTokenProvider;
import com.team6.backend.domain.user.dao.UserRepository;
import com.team6.backend.domain.user.dto.UserInfo;
import com.team6.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Value("${kakao.auth.client}")
	private String client_id;
	@Value("${kakao.auth.redirect}")
	private String redirect_uri;
	@Value("${kakao.auth.accessTokenURL}")
	private String access_token_url;
	@Value("${kakao.auth.userInfoURL}")
	private String user_info_url;

	@Transactional
	public AuthResponseDTO.LoginResponse kakaoLogin(String code) {
		// String accessToken = kakaoAuthProvider.

		ObjectMapper objectMapper = new ObjectMapper();
		// 카카오에 Access Token 요청
		RestTemplate restTemplate = new RestTemplate(); // http 통신을 위한 객체

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // 해당 명세에 맡게 파라미터 지정
		params.add("grant_type", "authorization_code");
		params.add("client_id", client_id);
		params.add("redirect_uri", redirect_uri);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				access_token_url,
				HttpMethod.POST,
				kakaoTokenRequest,
				String.class
			);

			Map<String, Object> responseMap = objectMapper.readValue(response.getBody(),
				new TypeReference<Map<String, Object>>() {
				});

			String kakaoAccessToken = (String)responseMap.get("access_token");
			UserInfo userInfo = getKakaoUserInfo(kakaoAccessToken);

			User user = userRepository.findByEmail(userInfo.email())
				.orElseGet(() -> userRepository.save(
					User.builder()
						.email(userInfo.email())
						.build()
				));

			String accessToken  = jwtTokenProvider.createAccessToken(user.getUserId());
			String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

			user.updateRefreshToken(refreshToken);
			userRepository.save(user);

			return new AuthResponseDTO.LoginResponse(
				user.getUserId(),
				new AuthResponseDTO.AuthTokens(accessToken, refreshToken)
			);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private UserInfo getKakaoUserInfo(String accessToken) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		ObjectMapper mapper = new ObjectMapper();

		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("secure_resource", "true");

		HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(user_info_url, entity, String.class);

		try {
			Map<String, Object> responseMap = mapper.readValue(response.getBody(),
				new TypeReference<Map<String, Object>>() {
				});
			Long userId = ((Number)responseMap.get("id")).longValue();

			Map<String, Object> kakaoAccount = (Map<String, Object>)responseMap.get("kakao_account");
			Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");

			String email = (String)kakaoAccount.get("email");

			UserInfo userInfo = UserInfo.builder()
				.userId(userId)
				.email((String)kakaoAccount.get("email"))
				.build();

			return userInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
