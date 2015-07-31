package net.amarantha.heating.hardware.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import net.amarantha.heating.entity.Status;
import net.amarantha.heating.hardware.ThermoTriggerListener;
import org.slf4j.Logger;

import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;
import static org.slf4j.LoggerFactory.getLogger;

@Singleton
public class RaspPiHeatingController extends AbstractHeatingController {

    private Logger logger = getLogger(RaspPiHeatingController.class);

    private GpioPinDigitalInput thermoTriggerPin;
    private GpioPinDigitalOutput heatingControlPin;

    @Inject
    public RaspPiHeatingController(GpioController gpio) {
        heatingControlPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        thermoTriggerPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
    }

    @Override
    public void init() {
        thermoTriggerPin.addListener((GpioPinListenerDigital) event -> {
            logger.info("Thermostat triggered " + event.getState().isLow());
            triggerThermo(event.getState().isLow() ? ON : OFF);
        });
    }

    @Override
    public void switchHeating(Status status) {
        if ( ON.equals(status) && heatingControlPin.isLow() ) {
            logger.info("Switch Heading ON");
            heatingControlPin.high();
        } else if ( OFF.equals(status) && heatingControlPin.isHigh() ) {
            logger.info("Switch Heading OFF");
            heatingControlPin.low();
        }
    }

    @Override
    public void setThermoTriggerListener(ThermoTriggerListener listener) {
        super.setThermoTriggerListener(listener);
        listener.onTriggerChanged(thermoTriggerPin.getState().isLow() ? ON : OFF);
    }

    @Override
    public Status getHeatingStatus() {
        return heatingControlPin.isHigh() ? ON : OFF;
    }

    @Override
    public Status getThermoTriggeredStatus() {
        return thermoTriggerPin.getState().isLow() ? ON : OFF;
    }
}
