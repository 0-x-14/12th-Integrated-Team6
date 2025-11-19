package com.team6.backend.domain.weather.dto.res;

import lombok.Builder;
import lombok.Getter;

public class OpenWeatherResponse {

    @Getter
    @Builder
    public static class WeatherDTO{
        private String city;
        private double temperature;
        private String description;
    }
}
