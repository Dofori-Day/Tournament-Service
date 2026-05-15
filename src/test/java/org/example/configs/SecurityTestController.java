package org.example.configs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {
    @GetMapping("/auth/test")
    public String authTest() { return "ok"; }

    @GetMapping("/api/test")
    public String apiTest() { return "ok"; }
}
