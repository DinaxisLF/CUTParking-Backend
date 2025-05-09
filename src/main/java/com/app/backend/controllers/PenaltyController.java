package com.app.backend.controllers;

import com.app.backend.dto.PenaltyDTO;
import com.app.backend.model.Penalty;
import com.app.backend.service.PenaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/penalties")
public class PenaltyController {

    @Autowired
    private PenaltyService penaltyService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PenaltyDTO>> getUserPenalties(@PathVariable int userId) {
        return ResponseEntity.ok(penaltyService.getPenaltyDTOsByUserId(userId));
    }

    @GetMapping("/penalty/{penaltyId}")
    public ResponseEntity<PenaltyDTO> getPenaltyById(@PathVariable int penaltyId) {
        Optional<PenaltyDTO> penaltyOpt = penaltyService.getPenaltyById(penaltyId);
        return penaltyOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
