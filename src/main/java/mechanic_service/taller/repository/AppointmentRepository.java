package mechanic_service.taller.repository;

import mechanic_service.taller.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByMechanicIdAndAppointmentDate(Long mechanicId, LocalDateTime appointmentDate);
}