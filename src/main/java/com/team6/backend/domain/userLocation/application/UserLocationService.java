package com.team6.backend.domain.userLocation.application;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team6.backend.api.dto.request.SaveLocationRequestDTO;
import com.team6.backend.domain.location.dao.LocationRepository;
import com.team6.backend.domain.location.entity.Location;
import com.team6.backend.domain.user.dao.UserRepository;
import com.team6.backend.domain.user.entity.User;
import com.team6.backend.domain.userLocation.dao.UserLocationRepository;
import com.team6.backend.domain.userLocation.entity.UserLocation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLocationService {

	private final LocationRepository locationRepository;
	private final UserRepository userRepository;
	private final UserLocationRepository userLocationRepository;

	@Transactional
	public Long saveLocation(Long userId, SaveLocationRequestDTO saveLocationRequestDTO) {
		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NoSuchElementException("User with ID " + userId + " does not exist"));

		Location location = locationRepository.save(
			Location.builder()
				.name(saveLocationRequestDTO.name())
				.lat(saveLocationRequestDTO.lat())
				.lng(saveLocationRequestDTO.lng())
				.build()
		);

		UserLocation userLocation = userLocationRepository.save(
			UserLocation.builder()
				.user(user)
				.location(location)
				.pinned(false)
				.build()
		);

		return userLocation.getUserLocationId();
	}
}
