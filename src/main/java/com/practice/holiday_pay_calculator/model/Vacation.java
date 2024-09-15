package com.practice.holiday_pay_calculator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vacation {
    private double averageMonthlySalary;
    private long vacationDays;
    private LocalDate startDate;
    private LocalDate endDate;
}
