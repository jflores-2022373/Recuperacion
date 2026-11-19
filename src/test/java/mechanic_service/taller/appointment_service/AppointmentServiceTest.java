package mechanic_service.taller.appointment_service;

import mechanic_service.taller.entity.Appointment;
import mechanic_service.taller.exception.BusinessValidationException;
import mechanic_service.taller.exception.ResourceNotFoundException;
import mechanic_service.taller.repository.AppointmentRepository;
import mechanic_service.taller.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("APT-01: Validar fecha pasada")
    void test1() {
        Appointment app = new Appointment(1L, 1L, "Juan", "P123", LocalDateTime.now().minusDays(1), "PENDING");
        assertThrows(BusinessValidationException.class, () -> appointmentService.createAppointment(app));
    }

    @Test
    @DisplayName("APT-02: Validar horario ocupado")
    void test2() {
        LocalDateTime future = LocalDateTime.now().plusDays(2);
        Appointment app = new Appointment(1L, 1L, "Ana", "P999", future, "PENDING");
        when(appointmentRepository.findByMechanicIdAndAppointmentDate(1L, future))
                .thenReturn(Collections.singletonList(app));
        assertThrows(BusinessValidationException.class, () -> appointmentService.createAppointment(app));
    }

    @Test
    @DisplayName("APT-03: Crear cita exitosa")
    void test3() {
        LocalDateTime future = LocalDateTime.now().plusDays(3);
        Appointment app = new Appointment(1L, 1L, "Carlos", "P456", future, "PENDING");
        when(appointmentRepository.findByMechanicIdAndAppointmentDate(1L, future))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(app);

        Appointment result = appointmentService.createAppointment(app);
        assertNotNull(result);
    }

    @Test
    @DisplayName("APT-04: Obtener todas las citas")
    void test4() {
        when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());
        List<Appointment> list = appointmentService.getAllAppointments();
        assertNotNull(list);
    }

    @Test
    @DisplayName("APT-05: Buscar cita por ID existente")
    void test5() {
        Appointment app = new Appointment(1L, 1L, "Maria", "P789", LocalDateTime.now().plusDays(1), "CONFIRMED");
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(app));
        Appointment found = appointmentService.getAppointmentById(1L);
        assertNotNull(found);
    }

    @Test
    @DisplayName("APT-06: Lanzar excepcion si cita no existe")
    void test6() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.getAppointmentById(99L));
    }

    @Test
    @DisplayName("APT-07: Intentar reactivar una cita CANCELADA a PROGRAMADA")
    void test7() {
        Appointment app = new Appointment(1L, 1L, "Pedro", "P321", LocalDateTime.now().minusDays(1), "CANCELLED");
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(BusinessValidationException.class, () -> {
            appointmentService.updateAppointmentStatus(1L, "PROGRAMADA");
        });
    }
}