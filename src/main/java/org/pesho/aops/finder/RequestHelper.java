package org.pesho.aops.finder;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestHelper {

    public static String getResponse(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Response status code: " + statusCode);

                String responseBody = EntityUtils.toString(response.getEntity());
                return responseBody;
            }
        }
    }

    public static String postRequest(String url, String... params) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);

            List<NameValuePair> nvps = new ArrayList<>();
            for (int i = 0; i < params.length; i+= 2) {
                nvps.add(new BasicNameValuePair(params[i], params[i+1]));
            }

            request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Response status code: " + statusCode);

                String responseBody = EntityUtils.toString(response.getEntity());
                return responseBody;
            }
        }
    }


}
