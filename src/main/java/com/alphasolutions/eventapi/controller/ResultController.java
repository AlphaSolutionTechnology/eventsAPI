package com.alphasolutions.eventapi.controller;

import com.alphasolutions.eventapi.model.Result;
import com.alphasolutions.eventapi.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping
    public ResponseEntity<Result> saveResult(@RequestBody Result result) {
        return ResponseEntity.ok(resultService.save(result));
    }
}
