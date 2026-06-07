package com.sap.cap.sflight.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Broken version — uses synchronized to create a hidden circular dependency:
 *   BookingLedger → PaymentGateway → SeatInventory → BookingLedger
 */
@Service
public class BookingLedgerService {

    @Autowired private PaymentGatewayService paymentGateway;

    // Holds BOOKING monitor, then acquires PAYMENT monitor inside.
    public synchronized void recordBooking(String passengerId) {
        System.out.println("BookingLedger: recording booking for " + passengerId);
        paymentGateway.chargePassenger(passengerId, 299.99);
    }

    // Entry point for the circular back-edge: SeatInventory calls this.
    public synchronized void finalizeBooking(String passengerId) {
        System.out.println("BookingLedger: finalizing booking for " + passengerId);
    }

    @Scheduled(fixedDelay = 200)
    public void backgroundAudit() {
        recordBooking("AUDIT-BGD");
    }
}
