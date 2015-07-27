package net.amarantha.heating.hardware;

import net.amarantha.heating.entity.Status;

public interface HeatingController {

    void init();

    void switchHeating(Status status);

    Status getHeatingStatus();

    void setThermoTriggerListener(ThermoTriggerListener listener);

    Status getThermoTriggeredStatus();

    void triggerThermo(Status status);

}
