package com.project.dailylog.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VueController {

    private static final Logger logger = LoggerFactory.getLogger(VueController.class);

    @RequestMapping(value = { "/dailylog/**" })
    public String index(HttpServletRequest request) {
        logger.info("Request URL: {}", request.getRequestURL());
        return "/vueStatic/index.html";
    }
}