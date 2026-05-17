package com.caravela21.palpiteiro.api.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ApiFootballQuotaGuardService {

    private LocalDate currentDay = LocalDate.now();
    private int consumedToday = 0;

    public synchronized boolean tryConsume(int amount, int dailyLimit) {
        LocalDate now = LocalDate.now();
        if (!now.equals(currentDay)) {
            currentDay = now;
            consumedToday = 0;
        }

        if (amount <= 0) {
            return true;
        }

        if (consumedToday + amount > dailyLimit) {
            return false;
        }

        consumedToday += amount;
        return true;
    }

    public synchronized int getConsumedToday() {
        LocalDate now = LocalDate.now();
        if (!now.equals(currentDay)) {
            currentDay = now;
            consumedToday = 0;
        }
        return consumedToday;
    }
}

