package ru.beeline.fdmnotificationsmanagement.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.beeline.fdmlib.dto.auth.EmailResponseDTO;
import ru.beeline.fdmnotificationsmanagement.exception.ForbiddenException;

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

}
