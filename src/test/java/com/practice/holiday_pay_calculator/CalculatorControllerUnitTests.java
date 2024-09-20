package com.practice.holiday_pay_calculator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.holiday_pay_calculator.controller.CalculatorController;
import com.practice.holiday_pay_calculator.model.Vacation;
import com.practice.holiday_pay_calculator.service.CalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CalculatorControllerUnitTests {
    private MockMvc mockMvc;

    @Mock
    private CalculatorService calculatorService;

    @InjectMocks
    private CalculatorController calculatorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(calculatorController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();
    }

    @Test
    void testGetHomePage() throws Exception {
        mockMvc.perform(get("/calculate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testCalculateVacationPay.csv")
    void testCalculateVacationPay(double averageMonthlySalary, long vacationDays, double expectedResult) throws Exception {
        Vacation vacation = new Vacation(averageMonthlySalary, vacationDays, null, null);
        when(calculatorService.getVacationPay(averageMonthlySalary, vacationDays, null, null)).thenReturn(expectedResult);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(vacation);

        MvcResult result = mockMvc.perform(post("/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"averageMonthlySalary\": 1000.0, \"vacationDays\": 10}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(responseContent);
        double actualResult = jsonNode.asDouble();

        assertEquals(expectedResult, actualResult, 0.01);
        String prettyJson = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        System.out.println(prettyJson);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCalculatorData")
    void testCalculateHolidayPayNegativeValues(Vacation data) throws Exception {
        when(calculatorService.getVacationPay(data.getAverageMonthlySalary(), data.getVacationDays(), data.getStartDate(), data.getEndDate()))
                .thenThrow(new IllegalArgumentException("Salary and vacation days must be positive."));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(data);

        mockMvc.perform(post("/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Salary and vacation days must be positive."));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/testCalculateHolidayPayNaNValues.csv")
    void testCalculateHolidayPayNaNValues(String salary, String days) throws Exception {
        String requestBody = String.format("{\"averageMonthlySalary\": \"%s\", \"vacationDays\": \"%s\"}", salary, days);

        mockMvc.perform(post("/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid JSON format. Please check your request body and input data."));
    }

    static Stream<Vacation> provideInvalidCalculatorData() {
        return Stream.of(
                new Vacation(-5000, 10, null, null),
                new Vacation(300.99, -10, null, null),
                new Vacation(-5000, -10, null, null)
        );
    }
}
