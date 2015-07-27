package net.amarantha.heating.hardware.impl;

import com.google.inject.Singleton;
import net.amarantha.heating.entity.Status;
import net.amarantha.heating.hardware.ThermoTriggerListener;

import static net.amarantha.heating.entity.Status.OFF;

@Singleton
public class MockHeatingController extends AbstractHeatingController {

    private Status heatingStatus = OFF;
    private Status thermoTriggeredStatus = OFF;

    private ThermoTriggerListener listener;

    @Override
    public void init() {}

    @Override
    public void switchHeating(Status status) {
        this.heatingStatus = status;
    }

    @Override
    public Status getHeatingStatus() {
        return heatingStatus;
    }

    @Override
    public void triggerThermo(Status status) {
        super.triggerThermo(status);
        thermoTriggeredStatus = status;
    }

    @Override
    public Status getThermoTriggeredStatus() {
        return thermoTriggeredStatus;
    }

}
