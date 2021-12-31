package PurpleIO.OembedAssignmentProject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OembedController {

    private final OembedProviderService oembedProviderService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/submitUrl")
    public String submitURL(@RequestParam String userURL) {
        System.out.println(userURL);
        return "index";
    }

    @GetMapping("/test")
    @ResponseBody
    public String testController() {
        String url = "https://vimeo.com/658304252";
        String encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8);
        Optional<OembedEndpoint> endpoint = oembedProviderService.findEndpoint(url);
        return endpoint.toString();
    }

    @GetMapping("/test2")
    @ResponseBody
    public String testController2() throws Exception {
        String url = "https://twitter.com/hellopolicy/status/867177144815804416";
        OembedResponse oembedResponse = oembedProviderService.getOembedResponse(url);
        String result = "" +oembedResponse.getTitle() + oembedResponse.getThumbnailUrl() + oembedResponse.getAuthorName() + oembedResponse.getAuthorUrl() + oembedResponse.getHtml() + oembedResponse.getType();
        return result;
    }

}
