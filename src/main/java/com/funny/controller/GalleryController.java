package com.funny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("gallery")
public class GalleryController {

    @RequestMapping("/index")
    public String index() {

        return null;
    }
}
