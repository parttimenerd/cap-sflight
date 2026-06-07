package com.sap.cap.sflight.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Broken version — holds PAYMENT monitor, then acquires INVENTORY monitor inside.
 */
@Service
public class PaymentGatewayService {

    @Autowired private SeatInventoryService seatInventory;

    public synchronized void chargePassenger(String passengerId, double amount) {
        System.out.println("PaymentGateway: charging " + passengerId + " $" + amount);
        seatInventory.confirmSeat(passengerId);
    }

    @Scheduled(fixedDelay = 200)
    public void backgroundReconcile() {
        chargePassenger("RECONCILE-BGD", 0.0);
    }
}
