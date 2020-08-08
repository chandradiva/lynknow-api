package com.lynknow.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lynknow.api.pojo.request.WablasSendMessageRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HttpRequestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtil.class);

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @Value("${wablas.api-key}")
    private String wablasApiKey;

    @Value("${wablas.endpoint.send-message}")
    private String sendMessageUrl;

    public String sendGet(WablasSendMessageRequest wablasRequest) throws Exception {
        try {
            URIBuilder builder = new URIBuilder(sendMessageUrl);
            builder.setParameter("token", wablasApiKey)
                    .setParameter("phone", wablasRequest.getPhone())
                    .setParameter("message", wablasRequest.getMessage());

            HttpGet request = new HttpGet(builder.build());
            request.addHeader("Content-Type", "application/json");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Get HttpResponse Status
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // return it as a String
                    return EntityUtils.toString(entity);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
        }

        return "";
    }

    public String sendPost(WablasSendMessageRequest wablasRequest) throws Exception {
        try {
            HttpPost request = new HttpPost(sendMessageUrl);

            request.addHeader("Authorization", wablasApiKey);
            request.addHeader("Content-Type", "application/json");

            ObjectMapper mapper = new ObjectMapper();

            HttpEntity entity = new ByteArrayEntity(mapper.writeValueAsBytes(wablasRequest));
            request.setEntity(entity);

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {

                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing data", e);
        }

        return "";
    }

}
