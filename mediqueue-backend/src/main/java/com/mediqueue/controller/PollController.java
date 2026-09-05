package com.mediqueue.controller;

import com.mediqueue.service.StreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poll")
public class PollController {

    private final StreamService streamService;

    public PollController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping("/{entity}")
    public ResponseEntity<Object> pollEntity(@PathVariable String entity, @RequestParam(name = "auth", required = false) String auth) {
        return ResponseEntity.ok(streamService.pollEntity(entity, auth));
    }
}
