package com.eden.comptab.web;

import com.eden.comptab.domain.Retour;
import com.eden.comptab.dto.RetourRequest;
import com.eden.comptab.dto.RetourResponse;
import com.eden.comptab.service.RetourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retours")
@RequiredArgsConstructor
public class RetourController {

    private final RetourService retourService;

    @PostMapping
    public ResponseEntity<RetourResponse> creerRetour(@RequestBody RetourRequest request) {
        Retour retour = retourService.traiterRetour(request);

        RetourResponse response = RetourResponse.builder()
                .id(retour.getId())
                .dateRetour(retour.getDateRetour())
                .montantRembourse(retour.getMontantRembourse())
                .raison(retour.getRaisonRetour())
                .venteOriginaleId(retour.getVenteInitiale().getId())
                .build();

        return ResponseEntity.ok(response);
    }
}

