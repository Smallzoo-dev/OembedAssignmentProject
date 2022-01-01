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
    private String author_name;
    //(optional)
    private String author_url;
    //(optional)
    private String provider_name;
    //(optional)
    private String provider_url;
    //(optional)
    private Long cache_age;
    //(optional)
    private String thumbnail_url;
    //(optional)
    private Integer thumbnail_width;
    //(optional)
    private Integer thumbnail_height;
    //(optional, but required in photo type)
    private String url;
    //(optional, but required in video, rich type)
    private String html;
    //(optional, but required in photo, video type)
    private Integer width;
    //(optional, but required in photo, video type)
    private Integer height;

}
