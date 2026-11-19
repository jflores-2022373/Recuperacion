package mechanic_service.taller;

import mechanic_service.taller.dto.MechanicRequest;
import mechanic_service.taller.entity.Mechanic;
import mechanic_service.taller.service.MechanicService;
import mechanic_service.taller.exception.BusinessValidationException;
import mechanic_service.taller.exception.ResourceNotFoundException;
import mechanic_service.taller.repository.MechanicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MechanicServiceTest {

    @Mock
    private MechanicRepository mechanicRepository;

    @InjectMocks
    private MechanicService mechanicService;

    private Mechanic activeMechanic;

    @BeforeEach
    void setUp() {
        activeMechanic = new Mechanic();
        activeMechanic.setId(1L);
        activeMechanic.setFullName("Carlos Méndez");
        activeMechanic.setSpecialty("FRENOS");
        activeMechanic.setBayCode("BAHIA-01");
        activeMechanic.setWorkStartTime(LocalTime.of(8, 0));
        activeMechanic.setWorkEndTime(LocalTime.of(17, 0));
        activeMechanic.setActive(true);
    }

    @Test
    @DisplayName("MEC-01: Debe crear mecánico cuando el horario laboral es válido")
    void createMechanic_ValidSchedule_ReturnsSavedMechanic() {
        MechanicRequest request = new MechanicRequest(
                "Carlos Méndez", "FRENOS", "BAHIA-01",
                LocalTime.of(8, 0), LocalTime.of(17, 0)
        );

        when(mechanicRepository.save(any(Mechanic.class))).thenReturn(activeMechanic);

        Mechanic result = mechanicService.create(request);

        assertNotNull(result);
        assertEquals("Carlos Méndez", result.getFullName());
        assertEquals("BAHIA-01", result.getBayCode());
        assertTrue(result.getActive());
        verify(mechanicRepository, times(1)).save(any(Mechanic.class));
    }

    @Test
    @DisplayName("MEC-02: Debe rechazar creación si workEndTime es anterior o igual a workStartTime")
    void createMechanic_InvalidSchedule_ThrowsException() {
        MechanicRequest invalidRequest = new MechanicRequest(
                "Luis Pérez", "MOTOR", "BAHIA-02",
                LocalTime.of(17, 0), LocalTime.of(8, 0)
        );

        assertThrows(BusinessValidationException.class, () -> mechanicService.create(invalidRequest));
        verify(mechanicRepository, never()).save(any());
    }

    @Test
    @DisplayName("MEC-03: Debe confirmar disponibilidad cuando el mecánico está activo y el rango cae en su turno")
    void isAvailable_WithinWorkingHoursAndActive_ReturnsTrue() {
        when(mechanicRepository.findById(1L)).thenReturn(Optional.of(activeMechanic));

        boolean available = mechanicService.isAvailable(1L, LocalTime.of(9, 0), 60);

        assertTrue(available);
    }

    @Test
    @DisplayName("MEC-04: Debe retornar false si la cita termina después de workEndTime")
    void isAvailable_ExceedsWorkEndTime_ReturnsFalse() {
        when(mechanicRepository.findById(1L)).thenReturn(Optional.of(activeMechanic));

        boolean available = mechanicService.isAvailable(1L, LocalTime.of(16, 30), 60);

        assertFalse(available);
    }

    @Test
    @DisplayName("MEC-05: Debe retornar false si el mecánico está inactivo")
    void isAvailable_InactiveMechanic_ReturnsFalse() {
        activeMechanic.setActive(false);
        when(mechanicRepository.findById(1L)).thenReturn(Optional.of(activeMechanic));

        boolean available = mechanicService.isAvailable(1L, LocalTime.of(10, 0), 45);

        assertFalse(available);
    }

    @Test
    @DisplayName("MEC-06: Debe lanzar ResourceNotFoundException si el mecánico no existe")
    void getById_NonExistentId_ThrowsNotFound() {
        when(mechanicRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> mechanicService.getById(99L));
    }
}