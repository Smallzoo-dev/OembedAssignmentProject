package PurpleIO.OembedAssignmentProject;


import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OembedProviderService {

    private final List<OembedEndpoint> endpoints;
    private final JSONParser jsonParser;
    private final AntPathMatcher antPathMatcher;
    private final PathPatternParser pathPatternParser;

    public OembedProviderService() {
        this.jsonParser = new JSONParser();
        this.endpoints = this.getEndpointsFromApi();
        this.antPathMatcher = new AntPathMatcher();
        this.pathPatternParser = new PathPatternParser();
    }


    public List<OembedEndpoint> getEndpointsFromApi() {
        final String providerURL = "https://oembed.com/providers.json";
        List<OembedEndpoint> endpoints = new ArrayList<>();

        try {
            URL url = new URL(providerURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            JSONArray jsonArray = (JSONArray) jsonParser.parse(bufferedReader);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject provider = (JSONObject) jsonArray.get(i);
                String endpointList = (String) provider.get("endpoints").toString();
                JSONArray endpointsJson = (JSONArray) jsonParser.parse(endpointList);
                ArrayList<String> schemesArr = new ArrayList<>();


                JSONObject endpointData = (JSONObject) endpointsJson.get(0);
                String endpointURL = (String) endpointData.get("url");
                JSONArray schemes = (JSONArray) endpointData.get("schemes");
                if (schemes != null) {
                    for (Object scheme : schemes) {
                        schemesArr.add((String) scheme);
                    }
                }

                OembedEndpoint oembedEndpoint = new OembedEndpoint();
                oembedEndpoint.setName((String) provider.get("provider_name"));
                oembedEndpoint.setEndpoint(endpointURL);
                oembedEndpoint.setUrlSchemes(schemesArr);
                System.out.println(oembedEndpoint.getUrlSchemes());

                endpoints.add(oembedEndpoint);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(endpoints.size());

        return endpoints;

    }

    public Optional<OembedEndpoint> findEndpoint(final String url) {
        Optional<OembedEndpoint> foundEndpoint = this.endpoints.stream()
                .filter(
                        endpoint -> endpoint
                                .getUrlSchemes()
                                .stream()
                                .map(String::trim)
                                .anyMatch(scheme -> antPathMatcher.match(scheme, url))
                )
                .findFirst();

        return foundEndpoint;
    }

    public OembedResponse getOembedResponse(String userRequestUrl) throws Exception {
        Optional<OembedEndpoint> endpoint = findEndpoint(userRequestUrl);
        if (!endpoint.isPresent()) {
            throw new Exception("No Endpoint found!!");
        }

        String apiUrl = endpoint.get().toApiUrl(userRequestUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);
        httpGet.addHeader("Content-Type", "application/son");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        String responseResult = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        System.out.println(responseResult);


        Gson gson = new Gson();
        return gson.fromJson(responseResult, OembedResponse.class);

    }


}
