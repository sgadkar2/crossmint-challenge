package com.crossmint.megaverse.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crossmint.megaverse.Service.MegaverseService;


@RestController
public class MegaVerseController {

    @Autowired
    private MegaverseService megaverseService;

    @PostMapping("/create-megaverse")
    public void createMegaVerse() {

        megaverseService.createMegaverse();
    }
}
