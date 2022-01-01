package PurpleIO.OembedAssignmentProject.service;


import PurpleIO.OembedAssignmentProject.domain.OembedEndpoint;
import PurpleIO.OembedAssignmentProject.domain.OembedResponse;
import PurpleIO.OembedAssignmentProject.exceptions.EndPointNotFountException;
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

import java.io.BufferedReader;
import java.io.IOException;
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
    private final Gson gson;

    private static final String providerUrl = "https://oembed.com/providers.json";


    public OembedProviderService() {
        this.jsonParser = new JSONParser();
        this.antPathMatcher = new AntPathMatcher();
        this.gson = new Gson();
        this.endpoints = this.getEndpointsFromApi();
    }

    /**
     * OembedProviderService 생성자에 포함되어 있다.
     * OembedProviderService 생성시 oembed.com/providers.json에서 provider 정보를 받아온다.
     * 받아온 Provider들의 기본정보, oembed endpoint 정보를 JSONParser로 파싱하여, OembedEndpoint 객체 생성
     * @return OembedEndpoint 객체 리스트
     */
    public List<OembedEndpoint> getEndpointsFromApi() {
        List<OembedEndpoint> endpoints = new ArrayList<>();

        try {
            URL url = new URL(providerUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            JSONArray oembedProviderResponseArray = (JSONArray) jsonParser.parse(bufferedReader);

            for (int i = 0; i < oembedProviderResponseArray.size(); i++) {
                JSONObject provider = (JSONObject) oembedProviderResponseArray.get(i);
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

                endpoints.add(oembedEndpoint);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return endpoints;

    }

    /**
     * 각각의 url scheme 을 Glob 패턴으로 사용하여
     * 매칭되는 Oembed 객체가 있다면 리턴한다.
     * @param url
     * @return Optional, OembedEndpoint 객체
     */
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

    /**
     * 매칭되는 OembedEndpoint객체가 있다면, 해당 객체로부터 endpoint와 url을 받아와 요청을 보낸다.
     * @param userRequestUrl
     * @return oembedResponse 객체
     */
    public OembedResponse getOembedResponse(String userRequestUrl) throws IOException {

            Optional<OembedEndpoint> endpoint = findEndpoint(userRequestUrl);
            OembedEndpoint oembedEndpoint = endpoint.orElseThrow(() ->
                    new EndPointNotFountException("요청하신 url과 매칭되는 provider를 찾지 못하였습니다."));
            String apiUrl = oembedEndpoint.toApiUrl(userRequestUrl);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(apiUrl);
            httpGet.addHeader("Content-Type", "application/son");
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            String responseResult = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            return gson.fromJson(responseResult, OembedResponse.class);


    }


}
