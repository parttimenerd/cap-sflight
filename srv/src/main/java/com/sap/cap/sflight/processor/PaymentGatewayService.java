package com.sap.cap.sflight.processor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.sap.cap.sflight.processor.SeatReservationService.PAYMENT_LOCK;
import static com.sap.cap.sflight.processor.SeatReservationService.TIMEOUT_SEC;

/**
 * Fixed version — no synchronized methods. All locking is coordinated by the controller.
 */
@Service
public class PaymentGatewayService {

    public void chargePassengerUnsync(String passengerId, double amount) {
        System.out.println("PaymentGateway: charging " + passengerId + " $" + amount);
    }

    @Scheduled(fixedDelay = 200)
    public void backgroundReconcile() {
        try {
            if (!PAYMENT_LOCK.tryLock(TIMEOUT_SEC, TimeUnit.SECONDS)) return;
            try {
                chargePassengerUnsync("RECONCILE-BGD", 0.0);
            } finally {
                PAYMENT_LOCK.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
