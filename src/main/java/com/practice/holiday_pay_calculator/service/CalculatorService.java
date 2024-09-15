package com.practice.holiday_pay_calculator.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class CalculatorService {
    private final List<LocalDate> holidays = Arrays.asList(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 1, 2),
            LocalDate.of(2024, 1, 7),
            LocalDate.of(2024, 2, 23),
            LocalDate.of(2024, 3, 8),
            LocalDate.of(2024, 5, 1),
            LocalDate.of(2024, 5, 9),
            LocalDate.of(2024, 6, 12),
            LocalDate.of(2024, 11, 4)
    );

    public double getVacationPay(double avgMonthSalary, long vacationDays, LocalDate startDate, LocalDate endDate) {
       if (avgMonthSalary < 0 || vacationDays < 0) {
            throw new IllegalArgumentException("Value cannot be negative.");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата начала отпуска не может быть позже даты окончания.");
        }

        if (vacationDays != 0) {
            return calculatePay(avgMonthSalary, vacationDays);
        }

        if (startDate != null && endDate != null) {
            long totalVacationDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            long holidayCount = holidays.stream()
                    .filter(holiday -> !holiday.isBefore(startDate) && !holiday.isAfter(endDate))
                    .count();

            if (holidayCount > 0) {
                LocalDate extendedEndDate = endDate;

                for (int i = 0; i < holidayCount; i++) {
                    extendedEndDate = extendedEndDate.plusDays(holidayCount);

                    while (holidays.contains(extendedEndDate)) {
                        extendedEndDate = extendedEndDate.plusDays(1);
                    }
                }

                totalVacationDays = ChronoUnit.DAYS.between(startDate, extendedEndDate) + 1;
            }

            return calculatePay(avgMonthSalary, totalVacationDays - holidayCount);
        }
        throw new IllegalArgumentException("Укажите либо количество дней отпуска, либо диапазон дат");
    }

    private double calculatePay(double avgMonthSalary, long vacationDays) {
        double avgHolidayPayPerDay = avgMonthSalary / 29.3;
        return Math.round((avgHolidayPayPerDay * vacationDays) * 100.0) / 100.0;
    }
}
