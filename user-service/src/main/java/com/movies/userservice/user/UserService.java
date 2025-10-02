package com.movies.userservice.user;


import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

   @Value("${keycloak.serverUrl}")
   private String serverUrl;

   @Value("${keycloak.realm}")
   private String realm;

   @Value("${keycloak.clientId}")
   private String clientId;

   @Value("${keycloak.clientSecret}")
   private String clientSecret;

//   @Value("${keycloak.username}")
//   private String username;
//
//   @Value("${keycloak.password}")
//   private String password;

   public int registerUser() {
       Keycloak keycloak = KeycloakBuilder.builder()
               .serverUrl(serverUrl)
               .realm(realm)
               .clientId(clientId)
               .clientSecret(clientSecret)
//               .username(username)
//               .password(password)
               .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
               .build();

        UserRepresentation user = new UserRepresentation();
        user.setUsername("hanwarai");
//        user.setFirstName("Han");
//        user.setLastName("Warai");
//        user.setEmail("han@example.com");
        user.setEnabled(true);

        Response response = keycloak.realm(realm)
                .users()
                .create(user);

        return response.getStatus();
    }
}
