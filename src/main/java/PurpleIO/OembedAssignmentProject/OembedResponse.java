package PurpleIO.OembedAssignmentProject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OembedResponse {

    //(required)
    private String type;
    //(required)
    private String version;
    //(optional)
    private String title;
    //(optional)
    private String authorName;
    //(optional)
    private String authorUrl;
    //(optional)
    private String providerName;
    //(optional)
    private String providerUrl;
    //(optional)
    private Long cacheAge;
    //(optional)
    private String thumbnailUrl;
    //(optional)
    private Integer thumbnailWidth;
    //(optional)
    private Integer thumbnailHeight;
    //(optional, but required in photo type)
    private String url;
    //(optional, but required in video, rich type)
    private String html;
    //(optional, but required in photo, video type)
    private Integer width;
    //(optional, but required in photo, video type)
    private Integer height;

}
