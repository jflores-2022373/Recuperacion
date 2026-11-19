package mechanic_service.taller.service;

import mechanic_service.taller.entity.Appointment;
import mechanic_service.taller.exception.BusinessValidationException;
import mechanic_service.taller.exception.ResourceNotFoundException;
import mechanic_service.taller.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));
    }

    public Appointment createAppointment(Appointment appointment) {
        // Regla: La fecha no puede ser en el pasado
        if (appointment.getAppointmentDate() != null && appointment.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new BusinessValidationException("La fecha de la cita no puede estar en el pasado.");
        }

        // Regla: Validar que el mecánico no tenga otra cita exactamente a la misma hora
        List<Appointment> existing = appointmentRepository.findByMechanicIdAndAppointmentDate(
                appointment.getMechanicId(), appointment.getAppointmentDate()
        );
        if (!existing.isEmpty()) {
            throw new BusinessValidationException("El mecánico ya tiene una cita asignada en este horario.");
        }

        if (appointment.getStatus() == null || appointment.getStatus().isEmpty()) {
            appointment.setStatus("PENDING");
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointmentStatus(Long id, String newStatus) {
        Appointment appointment = getAppointmentById(id);

        // Regla APT-07: Intentar reactivar una cita CANCELADA a PROGRAMADA
        if ("CANCELLED".equals(appointment.getStatus()) && "PROGRAMADA".equals(newStatus)) {
            throw new BusinessValidationException("No se puede reactivar una cita cancelada a programada.");
        }

        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.delete(appointment);
    }
}