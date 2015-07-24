package net.amarantha.heating.hardware;

import net.amarantha.heating.entity.Status;

public interface HeatingController {

    void init();

    void switchHeating(Status status);

    void setThermoTriggerListener(ThermoTriggerListener listener);

}
