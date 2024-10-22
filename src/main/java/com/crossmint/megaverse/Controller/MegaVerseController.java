package com.crossmint.megaverse.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crossmint.megaverse.Service.CreateMegaverse;

@RestController
public class MegaVerseController {

    @Autowired
    private CreateMegaverse createMegaverse;

    @PostMapping("/create-megaverse")
    public ResponseEntity<String> createMegaVerse() {

        createMegaverse.createMegaverse();

        return ResponseEntity.ok("MegaVerse created successfully");
    }
}
