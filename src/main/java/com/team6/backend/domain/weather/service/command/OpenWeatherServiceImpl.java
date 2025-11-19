package com.team6.backend.domain.weather.service.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.backend.common.exception.AppException;
import com.team6.backend.common.exception.ErrorCode;
import com.team6.backend.domain.weather.dto.res.OpenWeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class OpenWeatherServiceImpl implements OpenWeatherService {

    @Value("${openWeather.api.key}")
    private String apiKey;
    private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;


    @Override
    public OpenWeatherResponse.WeatherDTO getWeather(double lat, double lon){
        if (lat < -90.0 || lat > 90.0 || lon < -180.0 || lon > 180.0) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                // 위도
                .queryParam("lat", lat)
                // 경도
                .queryParam("lon", lon)
                // 섭씨로 반환
                .queryParam("units", "metric")
                // 한국어로 반환
                .queryParam("lang", "kr")
                // apikey
                .queryParam("appid", apiKey)
                .toUriString();

        try{
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            String city = root.path("name").asText();
            double temperature = root.path("main").path("temp").asDouble();
            String description = root.path("weather").get(0).path("description").asText();

            return OpenWeatherResponse.WeatherDTO.builder()
                    .city(city)
                    .temperature(temperature)
                    .description(description)
                    .build();
        }
        catch (Exception e){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
    }
}
