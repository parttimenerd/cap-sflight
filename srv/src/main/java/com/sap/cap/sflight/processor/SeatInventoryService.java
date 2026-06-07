package com.sap.cap.sflight.processor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.sap.cap.sflight.processor.SeatReservationService.INVENTORY_LOCK;
import static com.sap.cap.sflight.processor.SeatReservationService.TIMEOUT_SEC;

/**
 * Fixed version — no synchronized methods. All locking is coordinated by the controller.
 */
@Service
public class SeatInventoryService {

    public void confirmSeatUnsync(String passengerId) {
        System.out.println("SeatInventory: confirming seat for " + passengerId);
    }

    @Scheduled(fixedDelay = 200)
    public void backgroundSync() {
        try {
            if (!INVENTORY_LOCK.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)) return;
            try {
                confirmSeatUnsync("SYNC-BGD");
            } finally {
                INVENTORY_LOCK.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
