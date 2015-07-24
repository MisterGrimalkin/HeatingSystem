package net.amarantha.heating.hardware;

import net.amarantha.heating.entity.Status;

public interface ThermoTriggerListener {

    void onTriggerChanged(Status triggerValue);

}
