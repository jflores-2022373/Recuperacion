package mechanic_service.taller.dto;

import java.time.LocalTime;

public class MechanicRequest {
    private String fullName;
    private String specialty;
    private String bayCode;
    private LocalTime workStartTime;
    private LocalTime workEndTime;

    // Constructor vacío
    public MechanicRequest() {}

    // Constructor con todos los argumentos (necesario para las pruebas)
    public MechanicRequest(String fullName, String specialty, String bayCode, LocalTime workStartTime, LocalTime workEndTime) {
        this.fullName = fullName;
        this.specialty = specialty;
        this.bayCode = bayCode;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
    }

    // Getters y Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getBayCode() { return bayCode; }
    public void setBayCode(String bayCode) { this.bayCode = bayCode; }

    public LocalTime getWorkStartTime() { return workStartTime; }
    public void setWorkStartTime(LocalTime workStartTime) { this.workStartTime = workStartTime; }

    public LocalTime getWorkEndTime() { return workEndTime; }
    public void setWorkEndTime(LocalTime workEndTime) { this.workEndTime = workEndTime; }
}