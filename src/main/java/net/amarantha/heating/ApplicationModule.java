package net.amarantha.heating;

import com.google.inject.AbstractModule;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.hardware.impl.RaspPiHeatingController;

import javax.inject.Provider;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HeatingController.class).to(RaspPiHeatingController.class);
        bind(GpioController.class).toProvider(GpioProvider.class);
    }

    private static class GpioProvider implements Provider<GpioController> {
        @Override
        public GpioController get() {
            return GpioFactory.getInstance();
        }
    }

}
