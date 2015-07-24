package net.amarantha.heating.service;

import net.amarantha.heating.entity.Status;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.hardware.ThermoTriggerListener;

import static net.amarantha.heating.entity.Status.OFF;

public class MockHeatingController implements HeatingController {

    private Status status = OFF;
    private ThermoTriggerListener listener;

    public Status isOn() {
        return status;
    }

    public void triggerThermo(Status s) {
        if ( listener!=null ) {
            listener.onTriggerChanged(s);
        }
    }

    @Override
    public void init() {}

    @Override
    public void switchHeating(Status status) {
        this.status = status;
    }

    @Override
    public void setThermoTriggerListener(ThermoTriggerListener listener) {
        this.listener = listener;
    }

}
