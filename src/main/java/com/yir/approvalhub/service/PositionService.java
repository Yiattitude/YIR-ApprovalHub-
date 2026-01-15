package com.yir.approvalhub.service;

import com.yir.approvalhub.entity.Position;
import com.yir.approvalhub.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position getPositionById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found"));
    }

    @Transactional
    public Position createPosition(Position position) {
        if (positionRepository.existsByName(position.getName())) {
            throw new RuntimeException("Position name already exists");
        }
        return positionRepository.save(position);
    }

    @Transactional
    public Position updatePosition(Long id, Position positionDetails) {
        Position position = getPositionById(id);

        if (!position.getName().equals(positionDetails.getName()) 
                && positionRepository.existsByName(positionDetails.getName())) {
            throw new RuntimeException("Position name already exists");
        }

        position.setName(positionDetails.getName());
        position.setDescription(positionDetails.getDescription());
        position.setLevel(positionDetails.getLevel());
        position.setEnabled(positionDetails.getEnabled());

        return positionRepository.save(position);
    }

    @Transactional
    public void deletePosition(Long id) {
        Position position = getPositionById(id);
        positionRepository.delete(position);
    }

    public List<Position> getEnabledPositions() {
        return positionRepository.findByEnabled(true);
    }
}
