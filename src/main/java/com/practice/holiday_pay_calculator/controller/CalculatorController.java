package com.practice.holiday_pay_calculator.controller;

import com.practice.holiday_pay_calculator.model.Vacation;
import com.practice.holiday_pay_calculator.service.CalculatorService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculate")
public class CalculatorController {
    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping
    public ResponseEntity<Resource> getHomePage() {
        Resource resource = new ClassPathResource(("static/index.html"));
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/html"))
                .body(resource);
    }

    @PostMapping
    public ResponseEntity calculate(@RequestBody Vacation data) {
        double result = calculatorService.getVacationPay(
                data.getAverageMonthlySalary(),
                data.getVacationDays(),
                data.getStartDate(),
                data.getEndDate()
        );
        return ResponseEntity.ok(result);
    }
}
