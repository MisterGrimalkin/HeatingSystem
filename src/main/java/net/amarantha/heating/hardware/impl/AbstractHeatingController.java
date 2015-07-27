package net.amarantha.heating.hardware.impl;

import net.amarantha.heating.entity.Status;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.hardware.ThermoTriggerListener;

public abstract class AbstractHeatingController implements HeatingController {

    private ThermoTriggerListener listener;

    @Override
    public void setThermoTriggerListener(ThermoTriggerListener listener) {
        this.listener = listener;
        listener.onTriggerChanged(getThermoTriggeredStatus());
    }

    @Override
    public void triggerThermo(Status status) {
        if (listener != null) {
            listener.onTriggerChanged(status);
        }
    }

}
