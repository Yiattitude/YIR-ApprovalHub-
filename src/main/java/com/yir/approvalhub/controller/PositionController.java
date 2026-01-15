package com.yir.approvalhub.controller;

import com.yir.approvalhub.dto.ApiResponse;
import com.yir.approvalhub.entity.Position;
import com.yir.approvalhub.service.PositionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@CrossOrigin(origins = "*")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPositions() {
        try {
            List<Position> positions = positionService.getAllPositions();
            return ResponseEntity.ok(new ApiResponse(true, "Success", positions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPositionById(@PathVariable Long id) {
        try {
            Position position = positionService.getPositionById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Success", position));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPosition(@RequestBody Position position) {
        try {
            Position createdPosition = positionService.createPosition(position);
            return ResponseEntity.ok(new ApiResponse(true, "Position created successfully", createdPosition));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePosition(@PathVariable Long id, @RequestBody Position position) {
        try {
            Position updatedPosition = positionService.updatePosition(id, position);
            return ResponseEntity.ok(new ApiResponse(true, "Position updated successfully", updatedPosition));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePosition(@PathVariable Long id) {
        try {
            positionService.deletePosition(id);
            return ResponseEntity.ok(new ApiResponse(true, "Position deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}
