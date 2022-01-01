package PurpleIO.OembedAssignmentProject.web;

import PurpleIO.OembedAssignmentProject.domain.OembedResponse;
import PurpleIO.OembedAssignmentProject.service.OembedProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
                            Model model) {
        try {
            Map<String, String> oembedResponse = oembedProviderService.getOembedResponse(userRequestUrl);
            model.addAttribute("oembedResponse", oembedResponse);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("hasError", e.getMessage());
            return "index";
        }

    }

}
