package org.example.configs.security;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class Auth0Config {
    private static final Properties properties = new Properties();
    private static String cachedToken;
    private static long expirationTime = 0;
    static {
        try (InputStream input = Auth0Config.class.getClassLoader().getResourceAsStream("auth0.properties")) {
            if(input != null) {
                properties.load(input);
            }
            else {
                throw new RuntimeException("auth0.properties resource not found");
            }
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getManagementToken() throws IOException {
        if (cachedToken != null && System.currentTimeMillis() < expirationTime) {
            return cachedToken;
        }

        URL url = new URL(getProperty("AUTH0_DOMAIN") + "/oauth/token");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String requestBody = String.format("""
        {
          "client_id": "%s",
          "client_secret": "%s",
          "audience": "%s",
          "grant_type": "client_credentials"
        }
        """,getProperty("CLIENT_ID_REG"), getProperty("CLIENT_SECRET_REG"), getProperty("AUDIENCE"));

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to get token from Auth0: " + responseCode);
        }

        String jsonResponse = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(jsonResponse);
        cachedToken = json.getString("access_token");
        int expiresIn = json.getInt("expires_in");
        expirationTime = System.currentTimeMillis() + expiresIn * 1000L - 10_000L;

        return cachedToken;

    }
}
