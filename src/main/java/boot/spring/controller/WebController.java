package boot.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    /**
     * 首页，显示帖子列表页面
     */
    @GetMapping("/")
    public String index() {
        return "rich/index";
    }
    
    /**
     * 帖子页面
     */
    @GetMapping("/posts")
    public String posts() {
        return "rich/index";
    }
} 