package com.sap.cap.sflight.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Broken version — holds INVENTORY monitor, then acquires BOOKING monitor inside,
 * completing the 3-way circular dependency.
 */
@Service
public class SeatInventoryService {

    @Autowired private BookingLedgerService bookingLedger;

    public synchronized void confirmSeat(String passengerId) {
        System.out.println("SeatInventory: confirming seat for " + passengerId);
        // Back-edge that closes the cycle: INVENTORY → BOOKING
        bookingLedger.finalizeBooking(passengerId);
    }

    @Scheduled(fixedDelay = 200)
    public void backgroundSync() {
        confirmSeat("SYNC-BGD");
    }
}
