package com.sap.cap.sflight.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Broken version — triggers the booking path and surfaces the deadlock as a
 * FATAL error when the request hangs for more than 8 seconds.
 *
 * The deadlock cycle (BookingLedger → PaymentGateway → SeatInventory → BookingLedger)
 * is created by @Scheduled background tasks on the three service classes firing
 * every 200 ms while a request thread holds one of the locks.
 */
@RestController
public class SeatReservationService {

    @Autowired private BookingLedgerService bookingLedger;

    @GetMapping("/api/reserve-seat")
    public String reserveSeat() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            bookingLedger.recordBooking("PAX-4217");
            return "Reservation completed successfully.";
        });
        try {
            return future.get(8, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return "FATAL: Transaction 0x7F2A failed — unresolvable monitor contention.";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
