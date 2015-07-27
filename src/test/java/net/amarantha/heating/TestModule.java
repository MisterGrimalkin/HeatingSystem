package net.amarantha.heating;

import com.googlecode.guicebehave.AbstractStoryModule;
import net.amarantha.heating.hardware.HeatingController;
import net.amarantha.heating.hardware.impl.MockHeatingController;
import net.amarantha.heating.utility.MockPropertyManager;
import net.amarantha.heating.utility.PropertyManager;

public class TestModule extends AbstractStoryModule {

    @Override
    protected void configureStory() {
        bind(HeatingController.class).to(MockHeatingController.class);
        bind(PropertyManager.class).to(MockPropertyManager.class);
    }
}
