package net.amarantha.heating.hardware.impl;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import net.amarantha.heating.entity.Status;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.hardware.ThermoTriggerListener;

import static net.amarantha.heating.entity.Status.OFF;
import static net.amarantha.heating.entity.Status.ON;

public class RaspPiHeatingController implements HeatingController {

    private GpioPinDigitalInput thermoTriggerPin;
    private GpioPinDigitalOutput heatingControlPin;

    private ThermoTriggerListener listener;

    public RaspPiHeatingController() {
        GpioController gpio = GpioFactory.getInstance();
        heatingControlPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        thermoTriggerPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, PinPullResistance.PULL_UP);
    }

    @Override
    public void init() {
        thermoTriggerPin.addListener((GpioPinListenerDigital) event -> {
            System.out.println("Thermostat triggered " + event.getState().isLow());
            if (listener != null) {
                listener.onTriggerChanged(event.getState().isLow() ? ON : OFF);
            }
        });
    }

    @Override
    public void switchHeating(Status status) {
        if ( heatingControlPin.isHigh() && OFF.equals(status) ) {
           System.out.println("Switch Heading OFF");
            heatingControlPin.low();
        } else if ( heatingControlPin.isLow() && ON.equals(status) ) {
           System.out.println("Switch Heading ON");
            heatingControlPin.high();
        }
    }

    @Override
    public void setThermoTriggerListener(ThermoTriggerListener listener) {
        this.listener = listener;
        listener.onTriggerChanged(thermoTriggerPin.getState().isLow() ? ON : OFF);
    }

}
