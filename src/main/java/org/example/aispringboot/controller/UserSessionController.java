package org.example.aispringboot.controller;

import org.example.aispringboot.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserSessionController {
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }
}
