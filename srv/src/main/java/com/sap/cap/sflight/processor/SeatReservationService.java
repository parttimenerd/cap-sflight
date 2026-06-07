package com.sap.cap.sflight.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Fixed version — global lock ordering: BOOKING → PAYMENT → INVENTORY.
 * tryLock with a timeout prevents indefinite blocking on all code paths.
 */
@RestController
public class SeatReservationService {

    // Global lock ordering prevents circular waiting across all callers.
    static final ReentrantLock BOOKING_LOCK   = new ReentrantLock();
    static final ReentrantLock PAYMENT_LOCK   = new ReentrantLock();
    static final ReentrantLock INVENTORY_LOCK = new ReentrantLock();
    static final long TIMEOUT_SEC = 3;

    @Autowired private BookingLedgerService  bookingLedger;
    @Autowired private PaymentGatewayService paymentGateway;
    @Autowired private SeatInventoryService  seatInventory;

    @GetMapping("/api/reserve-seat")
    public String reserveSeat() {
        try {
            if (!BOOKING_LOCK.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)) return "retry";
            try {
                if (!PAYMENT_LOCK.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)) return "retry";
                try {
                    if (!INVENTORY_LOCK.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)) return "retry";
                    try {
                        bookingLedger.recordBookingUnsync("PAX-4217");
                        paymentGateway.chargePassengerUnsync("PAX-4217", 299.99);
                        seatInventory.confirmSeatUnsync("PAX-4217");
                        return "Reservation completed successfully.";
                    } finally { INVENTORY_LOCK.unlock(); }
                } finally { PAYMENT_LOCK.unlock(); }
            } finally { BOOKING_LOCK.unlock(); }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted.";
        }
    }
}
