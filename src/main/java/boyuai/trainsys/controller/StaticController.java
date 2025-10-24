package boyuai.trainsys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 静态资源控制器
 */
@Controller
public class StaticController {

    @GetMapping("/")
    public String index() {
        return "redirect:/static/index.html";
    }
}
