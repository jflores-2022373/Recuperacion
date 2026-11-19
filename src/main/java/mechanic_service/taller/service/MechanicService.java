package mechanic_service.taller.service;

import mechanic_service.taller.dto.MechanicRequest;
import mechanic_service.taller.entity.Mechanic;
import mechanic_service.taller.exception.BusinessValidationException;
import mechanic_service.taller.exception.ResourceNotFoundException;
import mechanic_service.taller.repository.MechanicRepository;
import org.springframework.stereotype.Service;
import java.time.LocalTime;

@Service
public class MechanicService {

    private final MechanicRepository mechanicRepository;

    public MechanicService(MechanicRepository mechanicRepository) {
        this.mechanicRepository = mechanicRepository;
    }

    public Mechanic create(MechanicRequest request) {
        if (request.getWorkEndTime() != null && request.getWorkStartTime() != null) {
            if (!request.getWorkEndTime().isAfter(request.getWorkStartTime())) {
                throw new BusinessValidationException("La hora de fin debe ser posterior a la hora de inicio");
            }
        }

        Mechanic mechanic = new Mechanic();
        mechanic.setFullName(request.getFullName());
        mechanic.setSpecialty(request.getSpecialty());
        mechanic.setBayCode(request.getBayCode());
        mechanic.setWorkStartTime(request.getWorkStartTime());
        mechanic.setWorkEndTime(request.getWorkEndTime());
        mechanic.setActive(true);

        return mechanicRepository.save(mechanic);
    }

    public Mechanic getById(Long id) {
        return mechanicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mecánico no encontrado con ID: " + id));
    }

    public boolean isAvailable(Long mechanicId, LocalTime startTime, int durationMinutes) {
        Mechanic mechanic = getById(mechanicId);

        if (!Boolean.TRUE.equals(mechanic.getActive())) {
            return false;
        }

        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        return !startTime.isBefore(mechanic.getWorkStartTime()) &&
                !endTime.isAfter(mechanic.getWorkEndTime());
    }
}