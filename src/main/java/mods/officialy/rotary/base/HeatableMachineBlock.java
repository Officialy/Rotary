package mods.officialy.rotary.base;

import mods.officialy.rotary.api.machine.Heatable;

public class HeatableMachineBlock extends RotaryMachineBlock implements Heatable {

    public HeatableMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setTemperature(float temperature) {

    }

    @Override
    public float getTemperature() {
        return 0;
    }

    @Override
    public float getMinimumOperatingTemperature() {
        return 0;
    }

    @Override
    public float getMaximumTemperature() {
        return 0;
    }
}
