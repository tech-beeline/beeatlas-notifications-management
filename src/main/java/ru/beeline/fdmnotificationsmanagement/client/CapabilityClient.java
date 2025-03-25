package ru.beeline.fdmnotificationsmanagement.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.beeline.fdmlib.dto.capability.BusinessCapabilityChildrenIdsDTO;
import ru.beeline.fdmnotificationsmanagement.dto.CapabilityParentDTO;
import ru.beeline.fdmnotificationsmanagement.exception.ServerNotFoundException;

@Slf4j
@Service
public class CapabilityClient {

    RestTemplate restTemplate;
    private final String capabilityServerUrl;

    public CapabilityClient(@Value("${integration.capability-server-url}") String capabilityServerUrl,
                            RestTemplate restTemplate) {
        this.capabilityServerUrl = capabilityServerUrl;
        this.restTemplate = restTemplate;
    }

    public CapabilityParentDTO getTechCapabilityParents(Integer entityId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            CapabilityParentDTO result = restTemplate.exchange(capabilityServerUrl + "/api/v1/tech-capabilities/" + entityId + "/parents",
                    HttpMethod.GET, entity, CapabilityParentDTO.class).getBody();
            if (result.getParents() == null && result.getParents().isEmpty()) {
                log.error("Parents not found for entityId = " + entityId);
            }
            return result;
        } catch (HttpClientErrorException.NotFound e) {
            log.error(e.getMessage());
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServerNotFoundException(e.getMessage());
        }
    }

    public CapabilityParentDTO getBusinessCapabilityParents(Integer entityId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            CapabilityParentDTO result = restTemplate.exchange(capabilityServerUrl + "/api/v1/business-capability/" + entityId + "/parents",
                    HttpMethod.GET, entity, CapabilityParentDTO.class).getBody();
            if (result.getParents() == null && result.getParents().isEmpty()) {
                log.error("Parents not found for entityId = " + entityId);
            }
            return result;
        } catch (HttpClientErrorException.NotFound e) {
            log.error(e.getMessage());
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ServerNotFoundException(e.getMessage());
        }
    }


    public BusinessCapabilityChildrenIdsDTO getBusinessCapabilityKidsById(Integer id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("SOURCE", "Sparx");

            HttpEntity<?> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(capabilityServerUrl + "/api/v1/business-capability/" + id + "/children/all",
                    HttpMethod.GET, entity, BusinessCapabilityChildrenIdsDTO.class).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
