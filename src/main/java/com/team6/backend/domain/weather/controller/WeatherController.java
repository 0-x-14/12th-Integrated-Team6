package com.team6.backend.domain.weather.controller;

import com.team6.backend.common.dto.DataResponse;
import com.team6.backend.domain.weather.dto.res.OpenWeatherResponse;
import com.team6.backend.domain.weather.service.command.OpenWeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WeatherController {

    private final OpenWeatherService accuWeatherService;

    @GetMapping("/weather")
    public ResponseEntity<DataResponse<OpenWeatherResponse.CurrentWeatherDTO>> getCurrentWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ){
        OpenWeatherResponse.CurrentWeatherDTO response = accuWeatherService.getCurrentWeather(lat, lon);

        return ResponseEntity.ok(DataResponse.from(response));
    }
}
