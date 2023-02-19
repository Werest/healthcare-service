package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

class MedicalServiceTest {

    private final PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);

    private final SendAlertService alertService = Mockito.mock(SendAlertService.class);

    private MedicalService medicalService;

    @BeforeEach
    void setUp() {
        PatientInfo patientInfo = new PatientInfo("1" ,"Константин", "Иванов", LocalDate.of(1990, 10, 4), new HealthInfo(new BigDecimal("36.6"), new BloodPressure(120, 80)));
        medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        Mockito.when(patientInfoRepository.getById(Mockito.anyString())).thenReturn(patientInfo);
    }

    @Test
    void checkBloodPressure() {
        medicalService.checkBloodPressure(Mockito.anyString(), new BloodPressure(120, 40));
        testNotNormal();
    }

    @Test
    void checkTemperature() {
        medicalService.checkTemperature(Mockito.anyString(), new BigDecimal("35"));
        testNotNormal();
    }

    void testNotNormal(){
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals("Warning, patient with id: 1, need help", argumentCaptor.getValue());

        Mockito.verify(alertService, Mockito.only()).send(Mockito.anyString());
        Mockito.verify(patientInfoRepository, Mockito.only()).getById(Mockito.anyString());
    }

    @Test
    void notCheckBloodPressure() {
        medicalService.checkBloodPressure(Mockito.anyString(), new BloodPressure(120, 80));
        testNormal();
    }

    @Test
    void notCheckTemperature() {
        medicalService.checkTemperature(Mockito.anyString(), new BigDecimal("36.8"));
        testNormal();
    }

    void testNormal(){
        Mockito.verify(patientInfoRepository, Mockito.only()).getById(Mockito.anyString());
        Mockito.verify(alertService, Mockito.never()).send(Mockito.anyString());
    }
}