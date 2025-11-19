package com.team6.backend.domain.weather.dto.res;

import lombok.Builder;
import lombok.Getter;

public class OpenWeatherResponse {

    @Getter
    @Builder
    public static class CurrentWeatherDTO {
        private String city;
        private double temperature;
        private String description;
        private double feelTemperature; //체감온도
        private int humidity; // 습도
        private double windSpeed; // 풍속
        private String windDirection; // 풍향 (각도)
        private String sunriseTime; // 일출 시간

        private String pm10; // 미세먼지
        private String pm25; // 초미세먼지

        private String uv; // 자외선
    }
}
