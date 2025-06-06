package boot.spring.controller;

import boot.spring.pagemodel.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api")
public class GreetingController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/greet")
    public AjaxResult greet() {
        Locale locale = LocaleContextHolder.getLocale();
        return AjaxResult.success(messageSource.getMessage("greeting", null, locale));
    }
}
