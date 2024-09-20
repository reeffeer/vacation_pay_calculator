package com.practice.holiday_pay_calculator.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculatorService {
    private final List<LocalDate> holidays = getDataFromCsv("src/main/resources/holidays.csv");

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

    private List<LocalDate> getDataFromCsv(String filePath) {
        List<LocalDate> list  = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                LocalDate date = LocalDate.parse(line);
                list.add(date);
            }
        } catch (IOException | DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
