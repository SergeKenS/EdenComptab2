package com.eden.comptab.web;

import com.eden.comptab.dto.DashboardResponse;
import com.eden.comptab.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{magasinId}")
    public ResponseEntity<DashboardResponse> getSynthese(@PathVariable Long magasinId) {
        DashboardResponse response = dashboardService.getSyntheseMagasin(magasinId);
        return ResponseEntity.ok(response);
    }
}

