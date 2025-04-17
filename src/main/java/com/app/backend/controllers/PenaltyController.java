package com.app.backend.controllers;

import com.app.backend.model.Penalty;
import com.app.backend.service.PenaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Penalty>> getUserPenalties(@PathVariable int userId) {
        return ResponseEntity.ok(penaltyService.getPenaltiesByUserId(userId));
    }
}
