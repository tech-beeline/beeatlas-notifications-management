/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.fdmnotificationsmanagement.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.beeline.fdmnotificationsmanagement.dto.auth.EmailResponseDTO;
import ru.beeline.fdmnotificationsmanagement.dto.auth.UserProfileShortDTO;
import ru.beeline.fdmnotificationsmanagement.exception.BadRequestException;
import ru.beeline.fdmnotificationsmanagement.exception.ForbiddenException;

import java.util.List;

@Slf4j
@Service
public class AuthClient {
    private final String authServerUrl;
    RestTemplate restTemplate;

    public AuthClient(@Value("${integration.auth-server-url}") String authServerUrl, RestTemplate restTemplate) {
        this.authServerUrl = authServerUrl;
        this.restTemplate = restTemplate;
    }

    public EmailResponseDTO getEmailByUserID(Integer userId) {
        EmailResponseDTO emailResponseDTO = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            emailResponseDTO = restTemplate.exchange(authServerUrl + "/api/v1/profiles/" + userId + "/email",
                    HttpMethod.GET, entity, EmailResponseDTO.class).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ForbiddenException("FORBIDDEN");
        }
        return emailResponseDTO;
    }

    public List<UserProfileShortDTO> getUserProfilesByRole(String role) {
        List<UserProfileShortDTO> profiles = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            profiles = restTemplate.exchange(authServerUrl + "/api/v1/user/role/" + role,
                    HttpMethod.GET, entity, new ParameterizedTypeReference<List<UserProfileShortDTO>>() {}).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException("Нет получателей для нотификаций");
        }
        return profiles;
    }
}
