package com.mediqueue.controller;

import com.mediqueue.service.StreamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

    private final StreamService streamService;

    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping(value = "/{entity}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEntity(@PathVariable String entity, @RequestParam(name = "auth", required = false) String auth) {
        return streamService.subscribeToEntity(entity, auth);
    }
}
