package com.sap.cap.sflight.processor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.sap.cap.sflight.processor.SeatReservationService.BOOKING_LOCK;
import static com.sap.cap.sflight.processor.SeatReservationService.TIMEOUT_SEC;

/**
 * Fixed version — no synchronized methods. All locking is done by the controller
 * using a globally ordered set of ReentrantLocks to prevent circular waiting.
 */
@Service
public class BookingLedgerService {

    public void recordBookingUnsync(String passengerId) {
        System.out.println("BookingLedger: recording booking for " + passengerId);
    }

    public void finalizeBookingUnsync(String passengerId) {
        System.out.println("BookingLedger: finalizing booking for " + passengerId);
    }

    @Scheduled(fixedDelay = 200)
    public void backgroundAudit() {
        try {
            if (!BOOKING_LOCK.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)) return;
            try {
                recordBookingUnsync("AUDIT-BGD");
            } finally {
                BOOKING_LOCK.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
