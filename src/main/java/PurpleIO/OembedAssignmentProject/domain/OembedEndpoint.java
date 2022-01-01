package PurpleIO.OembedAssignmentProject.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
public class OembedEndpoint {

    private String name;

    private List<String> urlSchemes;

    private String endpoint;

    private Format format = Format.json;

    /**
     * 객체가 가지고 있는 endpoint 정보와
     * 유저가 요청한 url을 받아서 oembed api 호출 url을 만들어준다.
     * @param userRequestUrl
     * @return oembed 호출 url
     */
    public String toApiUrl(final String userRequestUrl) {
        String endPointUrl;
        String apiUrl;

        if (this.endpoint.toLowerCase().contains(".{format}")) {
            endPointUrl = this.endpoint.replaceAll(Pattern.quote("{format}"), this.getFormat().toString());
        } else {
            endPointUrl = this.endpoint;
        }

        if (endPointUrl.endsWith("/")) {
            endPointUrl = endPointUrl.substring(0, endPointUrl.length() - 1);
        }

        if (this.endpoint.toLowerCase().contains("oembed.")) {
            apiUrl = endPointUrl + "?url=" + userRequestUrl;
        } else {
            apiUrl = endPointUrl + "?format=json&url=" + userRequestUrl;
        }

        return apiUrl;

    }

}
