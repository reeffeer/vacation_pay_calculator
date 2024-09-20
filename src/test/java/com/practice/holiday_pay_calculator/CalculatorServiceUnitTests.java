package com.practice.holiday_pay_calculator;

import com.practice.holiday_pay_calculator.service.CalculatorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CalculatorServiceUnitTests {

    @Mock
    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testGetHolidayPay.csv")
    void testGetHolidayPay(double avgMonthSalary, int vacationDays, double expectedHolidayPay) {
        when(calculatorService.getVacationPay(avgMonthSalary, vacationDays, null, null)).thenReturn(expectedHolidayPay);
        double holidayPay = calculatorService.getVacationPay(avgMonthSalary, vacationDays, null, null);
        assertEquals(expectedHolidayPay, holidayPay, 0.01);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, -0.01, -3000.999})
    void testGetHolidayPayNegativeValues(double avgMonthSalary) {
        int vacationDays = 10;
        when(calculatorService.getVacationPay(avgMonthSalary, vacationDays, null, null))
                .thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> calculatorService.getVacationPay(avgMonthSalary, vacationDays, null, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, -20, -30})
    void testGetHolidayPayNegativeVacationDays(int vacationDays) {
        double avgMonthSalary = 1000.0;
        when(calculatorService.getVacationPay(avgMonthSalary, vacationDays, null, null))
                .thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> calculatorService.getVacationPay(avgMonthSalary, vacationDays, null, null));
    }
}
