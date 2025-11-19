package com.team6.backend.domain.weather.controller;

import com.team6.backend.common.dto.DataResponse;
import com.team6.backend.domain.weather.dto.res.OpenWeatherResponse;
import com.team6.backend.domain.weather.service.command.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    //현재 날씨 불러오기
    @GetMapping("/weather")
    public ResponseEntity<DataResponse<OpenWeatherResponse.CurrentWeatherDTO>> getCurrentWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ){
        OpenWeatherResponse.CurrentWeatherDTO response = weatherService.getCurrentWeather(lat, lon);

        return ResponseEntity.ok(DataResponse.from(response));
    }

    //시간별 날씨 불러오기
    @GetMapping("/forecast/hourly")
    public ResponseEntity<DataResponse<OpenWeatherResponse.HourWeatherDTO>> getHourlyWeather(
            @RequestParam double lat,
            @RequestParam double lon
    ){
        OpenWeatherResponse.HourWeatherDTO response = weatherService.getHourlyWeather(lat, lon);

        return ResponseEntity.ok(DataResponse.from(response));
    }

    //일별 날씨 불러오기
}
