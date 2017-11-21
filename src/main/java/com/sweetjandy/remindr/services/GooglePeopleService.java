package com.sweetjandy.remindr.services;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


@Service
public class GooglePeopleService {

    @Value("${google.people.clientId}")
    private String clientId;

    @Value("${google.people.clientSecret}")
    private String clientSecret;

    @Value("${google.people.redirectUrl}")
    private String redirectUrl;

    public String setUp() throws IOException {

        String scope = "https://www.googleapis.com/auth/contacts.readonly";

        // Step 1: Authorize -->
        String authorizationUrl =
                new GoogleBrowserClientRequestUrl(clientId, redirectUrl, Arrays.asList(scope)).build();

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
//        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("What is the authorization code?");
//        String code = in.readLine();
//        // End of Step 1 <--
//
//        // Step 2: Exchange -->
//        contacts(code);

        return authorizationUrl;

    }

    public List<Person> contacts(String code) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

//        GoogleTokenResponse tokenResponse =
//                new GoogleAuthorizationCodeTokenRequest(
//                        httpTransport, jsonFactory, clientId, clientSecret, code, redirectUrl)
//                        .execute();
        // End of Step 2 <--

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(code);
//                .setFromTokenResponse(tokenResponse);


        PeopleService peopleService = new PeopleService.Builder(httpTransport, jsonFactory, credential).build();

        // show all contacts
        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
                .setPersonFields("names,emailAddresses")
                .execute();
        return response.getConnections();
    }
}
