package com.example.skillsh.web;

import com.example.skillsh.domain.dto.payment.OfferRequestDTO;
import com.example.skillsh.domain.dto.payment.OfferResponseDTO;
import com.example.skillsh.services.AdminServiceOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/offers")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminServiceOfferController {

    private final AdminServiceOfferService adminServiceOfferService;

    public AdminServiceOfferController(AdminServiceOfferService adminServiceOfferService) {
        this.adminServiceOfferService = adminServiceOfferService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<OfferResponseDTO>> getAllOffers() {
        return ResponseEntity.ok(adminServiceOfferService.getAllOffers());
    }

    @PostMapping("/create")
    public ResponseEntity<OfferResponseDTO> createOffer(@RequestBody OfferRequestDTO dto) {
        return ResponseEntity.ok(adminServiceOfferService.createOffer(dto));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OfferResponseDTO> updateOffer(@PathVariable Long id, @RequestBody OfferRequestDTO dto) {
        return ResponseEntity.ok(adminServiceOfferService.updateOffer(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        adminServiceOfferService.deleteOffer(id);
        return ResponseEntity.ok().build();
    }
}
