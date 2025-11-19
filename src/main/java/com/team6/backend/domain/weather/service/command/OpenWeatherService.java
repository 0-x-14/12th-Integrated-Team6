package com.team6.backend.domain.weather.service.command;

import com.team6.backend.domain.weather.dto.res.OpenWeatherResponse;

public interface OpenWeatherService {

    //위도 경도를 이용하여 지역이름과 날씨 반환
    public OpenWeatherResponse.CurrentWeatherDTO getCurrentWeather(double lat, double lon);
}
