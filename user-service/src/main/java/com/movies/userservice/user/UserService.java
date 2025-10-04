package com.movies.userservice.user;


import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
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

   public String registerUser(KeycloakUser user) {
       Keycloak keycloak = KeycloakBuilder.builder()
              .serverUrl(serverUrl)
              .realm(realm)
              .clientId(clientId)
              .clientSecret(clientSecret)
//              .username(username)
//              .password(password)
              .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
              .build();

       UserRepresentation newUser = new UserRepresentation();
       newUser.setUsername(user.username());
//       user.setFirstName("Han");
//       user.setLastName("Warai");
//       user.setEmail("han@example.com");
       newUser.setEnabled(true);
       Response response = keycloak.realm(realm)
                .users()
                .create(newUser);

       //Getting the newly created user's ID.
       String userId = CreatedResponseUtil.getCreatedId(response);
       RealmResource realmResource = keycloak.realm(realm);

       setPasswordForUser(realmResource,user.password(),userId);
       setRolesForUsers(realmResource,userId);
       return userId;
    }

   // can also change the existing user's password.
   private void setPasswordForUser(RealmResource resource, String password, String userId){
       CredentialRepresentation passwordCred = new CredentialRepresentation();
       passwordCred.setTemporary(false);
       passwordCred.setType(CredentialRepresentation.PASSWORD);
       passwordCred.setValue(password);

       resource.users().get(userId).resetPassword(passwordCred);
       log.info("Password added to the user's keycloak profile with ID: {}", userId);
   }

   private void setRolesForUsers(RealmResource resource, String userId){
       UserResource userResource = resource.users().get(userId);
       RoleRepresentation realmRole = resource
               .roles()
               .get("USER")
               .toRepresentation();
       userResource.roles().realmLevel().add(List.of(realmRole));
       log.info("USER role added to the user with ID: {}",userId);
   }
}
