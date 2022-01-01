package PurpleIO.OembedAssignmentProject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String submitURL(@RequestParam("userRequestUrl") String userRequestUrl,
                            Model model) throws Exception {
        OembedResponse oembedResponse = oembedProviderService.getOembedResponse(userRequestUrl);
        model.addAttribute("oembedResponse", oembedResponse);
        return "index";
    }

}
