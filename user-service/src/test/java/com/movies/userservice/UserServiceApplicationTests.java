package com.movies.userservice;

import com.movies.userservice.user.KeycloakUser;
import com.movies.userservice.user.UserService;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.keycloak.admin.client.KeycloakBuilder.*;

@Testcontainers
class UserServiceApplicationTests {
    private static final String REALM_IMPORT_FILE = "/upcomingMovies-realm.json";
    private static final String IMAGE = "quay.io/keycloak/keycloak:26.3.4";
    private static final String REALM_NAME = "upcomingMovies";
    private static final String CLIENT_ID = "upcomingmovies-users";
    private static final String CLIENT_SECRET = System.getenv("CLIENT_SECRET");

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer(IMAGE)
            .withRealmImportFile(REALM_IMPORT_FILE)
            .withEnv("KC_FEATURES", "scripts");

    private UserService userService;
    private Keycloak adminClient;

    @BeforeEach
    void setUp(){
        userService = new UserService();

        try{
            setField(userService,"serverUrl",keycloak.getAuthServerUrl());
            setField(userService, "realm", REALM_NAME);
            setField(userService, "clientId", CLIENT_ID);
            setField(userService, "clientSecret", CLIENT_SECRET);
        }catch (NoSuchFieldException | IllegalAccessException e){
            throw new RuntimeException("Failed to set fields on UserService",e);
        }
        adminClient = builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .username("admin")
                .password("admin")
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        // --- THIS IS THE CRUCIAL PART ---
// Get the realm-management client's roles
        ClientRepresentation realmMgmtClientRep = adminClient.realm(REALM_NAME).clients().findByClientId("realm-management").get(0);
        ClientResource realmMgmtClient = adminClient.realm(REALM_NAME).clients().get(realmMgmtClientRep.getId());
        RoleRepresentation viewUsersRole = realmMgmtClient.roles().get("view-users").toRepresentation();
        RoleRepresentation manageUsersRole = realmMgmtClient.roles().get("manage-users").toRepresentation();
        RoleRepresentation viewRealmRole = realmMgmtClient.roles().get("view-realm").toRepresentation();

// Get your client's service account user
        ClientRepresentation yourClientRep = adminClient.realm(REALM_NAME).clients().findByClientId(CLIENT_ID).get(0);
        ClientResource yourClient = adminClient.realm(REALM_NAME).clients().get(yourClientRep.getId());
        String serviceAccountUserId = yourClient.getServiceAccountUser().getId();
        UserResource serviceAccountUser = adminClient.realm(REALM_NAME).users().get(serviceAccountUserId);

// Assign the roles to the service account
        serviceAccountUser.roles().clientLevel(realmMgmtClientRep.getId()).add(List.of(viewUsersRole, manageUsersRole, viewRealmRole));
    }
    private void setField(Object target, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target,value);
    }

    @Test
    void testRegisterUser_createsUserSuccessfullyAndAddsRole(){
        //Given
        KeycloakUser newUser = new KeycloakUser("testuser","testpassword");
        //When
        String userID = userService.registerUser(newUser);
        //Then
        assertThat(userID).isNotNull();

        UserResource userResource = adminClient.realm(REALM_NAME).users().get(userID);
        UserRepresentation userRepresentation = userResource.toRepresentation();

        assertThat(userRepresentation.getUsername()).isEqualTo(newUser.username());
        assertThat(userRepresentation.isEnabled()).isTrue();

        List<String> realmRoles = userResource.roles().realmLevel().listAll()
                .stream()
                .map(RoleRepresentation::getName)
                .toList();
        assertThat(realmRoles).contains("USER");
    }
}
