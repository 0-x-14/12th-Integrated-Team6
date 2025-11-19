package com.team6.backend.domain.weather.service.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.backend.domain.weather.dto.res.OpenWeatherResponse;
import com.team6.backend.domain.weather.exception.WeatherException;
import com.team6.backend.domain.weather.exception.code.WeatherErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OpenWeatherServiceImpl implements OpenWeatherService {

    @Value("${openWeather.api.key}")
    private String openWeatherApiKey;
    @Value("${weather.api.key}")
    private String weatherApiKey;
    private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;


    @Override
    public OpenWeatherResponse.CurrentWeatherDTO getCurrentWeather(double lat, double lon){
        // 잘못된 위도 경도 처리
        if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
            throw new WeatherException(WeatherErrorCode.INVALID_LAN_LON);
        }

        //현재 날씨 api
        String weatherUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                // 위도
                .queryParam("lat", lat)
                // 경도
                .queryParam("lon", lon)
                // 섭씨로 반환
                .queryParam("units", "metric")
                // 한국어로 반환
                .queryParam("lang", "kr")
                // apikey
                .queryParam("appid", openWeatherApiKey)
                .toUriString();

        JsonNode weatherRoot;
        try{
            String response = restTemplate.getForObject(weatherUrl, String.class);
            weatherRoot = objectMapper.readTree(response);
        }
        catch (Exception e){
            throw new WeatherException(WeatherErrorCode.WEATHER_FETCH_FAILED);
        }

        // 대기 질 api
        String airUrl = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/air_pollution")
            .queryParam("lat", lat)
            .queryParam("lon", lon)
            .queryParam("appid", openWeatherApiKey)
            .toUriString();
        JsonNode airRoot;
        try{
            String response = restTemplate.getForObject(airUrl, String.class);
            airRoot = objectMapper.readTree(response);
        }
        catch (Exception e){
            throw new WeatherException(WeatherErrorCode.WEATHER_FETCH_FAILED);
        }

        // 자외선 api
        String uvUrl = UriComponentsBuilder.fromHttpUrl("https://api.weatherapi.com/v1/current.json")
                .queryParam("key", weatherApiKey)
                .queryParam("q", lat + ", " + "lon")
                .toUriString();
        JsonNode uvRoot;
        try {
            String response = restTemplate.getForObject(uvUrl, String.class);
            uvRoot = objectMapper.readTree(response);
        }
        catch(Exception e){
            throw new WeatherException(WeatherErrorCode.UV_FETCH_FAILED);
        }

        // 기본적인 날씨 정보
        String city = weatherRoot.path("name").asText();
        double temperature = weatherRoot.path("main").path("temp").asDouble();
        String description = weatherRoot.path("weather").get(0).path("description").asText();

        double feelTemperature = weatherRoot.path("main").path("feels_like").asDouble();
        int humidity = weatherRoot.path("main").path("humidity").asInt();
        double windSpeed = weatherRoot.path("wind").path("speed").asDouble();
        int windDeg = weatherRoot.path("wind").path("deg").asInt();
        long sunrise = weatherRoot.path("sys").path("sunrise").asLong();

        // 대기질 정보(미세먼지)
        double pm10 = airRoot.path("list").get(0).path("component").path("pm10").asDouble();
        double pm25 = airRoot.path("list").get(0).path("component").path("pm25").asDouble();

        //자외선 정보
        double uv = uvRoot.path("current").path("uv").asDouble();

        return OpenWeatherResponse.CurrentWeatherDTO.builder()
                .city(city)
                .temperature(temperature)
                .description(description)
                .feelTemperature(feelTemperature)
                .humidity(humidity)
                .windSpeed(windSpeed)
                .windDirection(convertWindDegToDir(windDeg))
                .sunriseTime(formatUnixTime(sunrise))
                .pm10(convertPm10ToGrade(pm10))
                .pm25(convertPm25ToGrade(pm25))
                .uv(convertUvToGrade(uv))
                .build();
    }

    // 1. 풍향 변환기 (각도 -> 8방위 한글)
    private String convertWindDegToDir(int deg) {
        if (deg >= 22.5 && deg < 67.5) return "북동풍";
        if (deg >= 67.5 && deg < 112.5) return "동풍";
        if (deg >= 112.5 && deg < 157.5) return "남동풍";
        if (deg >= 157.5 && deg < 202.5) return "남풍";
        if (deg >= 202.5 && deg < 247.5) return "남서풍";
        if (deg >= 247.5 && deg < 292.5) return "서풍";
        if (deg >= 292.5 && deg < 337.5) return "북서풍";
        return "북풍";
    }

    // 미세먼지(PM10) 등급 변환기
    private String convertPm10ToGrade(double value) {
        if (value <= 30) return "좋음";
        if (value <= 80) return "보통";
        if (value <= 150) return "나쁨";
        return "매우나쁨";
    }

    // 초미세먼지(PM2.5) 등급 변환기
    private String convertPm25ToGrade(double value) {
        if (value <= 15) return "좋음";
        if (value <= 35) return "보통";
        if (value <= 75) return "나쁨";
        return "매우나쁨";
    }

    // 시간 변환기 (Unix -> HH:mm)
    private String formatUnixTime(long unixTime) {
        return Instant.ofEpochSecond(unixTime)
                .atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // 자외선 등급 변환기
    private String convertUvToGrade(double uv) {
        if (uv < 3.0) return "낮음";
        if (uv < 6.0) return "보통";
        if (uv < 8.0) return "높음";
        if (uv < 11.0) return "매우높음";
        return "위험";
    }
}
