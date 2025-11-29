package com.enspy26.gi.external_api.proxies;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.enspy26.gi.external_api.DTO.responses.AgenceResponseDTO;

@Service
public class OrganisationProxies {
  private final String AUTH_SERVICE_URL = "https://gateway.yowyob.com/organization-service";

  @Autowired
  private RestTemplate restTemplate;

  public AgenceResponseDTO getUser(UUID organizationId, UUID agencyId) {
    String url = AUTH_SERVICE_URL + "/organizations/" + organizationId + "/agencies/" + agencyId;

    return restTemplate.exchange(url, HttpMethod.GET, null, AgenceResponseDTO.class).getBody();
  }
}
