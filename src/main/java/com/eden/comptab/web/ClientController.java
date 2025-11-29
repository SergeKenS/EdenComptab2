package com.eden.comptab.web;

import com.eden.comptab.domain.Client;
import com.eden.comptab.domain.CompteClient;
import com.eden.comptab.domain.Magasin;
import com.eden.comptab.dto.ClientRequest;
import com.eden.comptab.repository.ClientRepository;
import com.eden.comptab.repository.CompteClientRepository;
import com.eden.comptab.repository.MagasinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepository;
    private final MagasinRepository magasinRepository;
    private final CompteClientRepository compteClientRepository;

    @PostMapping
    public ResponseEntity<Client> creerClient(@RequestBody ClientRequest request) {
        Magasin magasin = magasinRepository.findById(request.getMagasinId())
                .orElseThrow(() -> new RuntimeException("Magasin introuvable"));

        Client client = Client.builder()
                .nomComplet(request.getNomComplet())
                .tel(request.getTel())
                .email(request.getEmail())
                .magasin(magasin)
                .statutFidelite("Standard")
                .build();

        return ResponseEntity.ok(clientRepository.save(client));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listerClients(@RequestParam Long magasinId) {
        List<Client> clients = clientRepository.findByMagasinId(magasinId);

        // On transforme en Map pour inclure la dette actuelle sans créer un DTO spécifique lourd
        List<Map<String, Object>> response = clients.stream().map(client -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", client.getId());
            map.put("nomComplet", client.getNomComplet());
            map.put("tel", client.getTel());
            map.put("email", client.getEmail());
            map.put("statutFidelite", client.getStatutFidelite());
            
            // Récupération de la dette
            Optional<CompteClient> compte = compteClientRepository.findByClientIdAndMagasinId(client.getId(), magasinId);
            map.put("detteActuelle", compte.map(CompteClient::getSoldeActuel).orElse(BigDecimal.ZERO));
            
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable Long id) {
        return clientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

