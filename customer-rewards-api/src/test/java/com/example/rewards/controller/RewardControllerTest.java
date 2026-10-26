package com.example.rewards.controller;

import com.example.rewards.dto.RewardSummaryDto;
import com.example.rewards.exception.CustomerNotFoundException;
import com.example.rewards.service.AsyncTransactionService;
import com.example.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
@Import(com.example.rewards.exception.GlobalExceptionHandler.class)
class RewardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @MockBean
    private AsyncTransactionService asyncTransactionService;

    @Test
    void returnsRewardSummary() throws Exception {
        RewardSummaryDto summary = new RewardSummaryDto(
                101L, "Alice Johnson", "alice@example.com",
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 10, 31),
                3, "USD", 1, new BigDecimal("120.00"), 90, List.of());
        when(rewardService.getRewardSummary(101L, 3)).thenReturn(summary);

        mockMvc.perform(get("/api/v1/customers/101/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice Johnson"))
                .andExpect(jsonPath("$.totalRewardPoints").value(90));
    }

    @Test
    void rejectsInvalidMonthCount() throws Exception {
        mockMvc.perform(get("/api/v1/customers/101/rewards").param("months", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundForUnknownCustomer() throws Exception {
        when(rewardService.getRewardSummary(999L, 3))
                .thenThrow(new CustomerNotFoundException(999L));

        mockMvc.perform(get("/api/v1/customers/999/rewards"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
