package com.team6.backend.domain.userLocation.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team6.backend.domain.userLocation.entity.UserLocation;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
}
