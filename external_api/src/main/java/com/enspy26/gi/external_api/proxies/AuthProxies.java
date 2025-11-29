package com.enspy26.gi.external_api.proxies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.enspy26.gi.external_api.DTO.responses.UserResponseDTO;

@Service
public class AuthProxies {
  private final String AUTH_SERVICE_URL = "https://gateway.yowyob.com/auth-service";

  @Autowired
  private RestTemplate restTemplate;

  public UserResponseDTO getUser(String token, String username) {
    String url = AUTH_SERVICE_URL + "/auth/user/username/" + username;

    HttpHeaders headers = new HttpHeaders();
    if (StringUtils.hasText(token)) {
      headers.set("Authorization", token);
    }
    HttpEntity<?> entity = new HttpEntity<>(headers);
    return restTemplate.exchange(url, HttpMethod.GET, entity, UserResponseDTO.class).getBody();
  }
}
