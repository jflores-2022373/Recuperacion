package mechanic_service.taller.controller;

import mechanic_service.taller.dto.MechanicRequest;
import mechanic_service.taller.entity.Mechanic;
import mechanic_service.taller.service.MechanicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/mechanics")
public class MechanicController {

    private final MechanicService mechanicService;

    public MechanicController(MechanicService mechanicService) {
        this.mechanicService = mechanicService;
    }

    @PostMapping
    public ResponseEntity<Mechanic> createMechanic(@RequestBody MechanicRequest request) {
        Mechanic created = mechanicService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mechanic> getMechanicById(@PathVariable Long id) {
        Mechanic mechanic = mechanicService.getById(id);
        return ResponseEntity.ok(mechanic);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam LocalTime startTime,
            @RequestParam int durationMinutes) {
        boolean available = mechanicService.isAvailable(id, startTime, durationMinutes);
        return ResponseEntity.ok(available);
    }
}