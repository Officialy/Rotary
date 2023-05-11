package mods.officialy.rotary.common.block.entity.machine;

import mods.officialy.rotary.api.power.ShaftEmitter;
import mods.officialy.rotary.base.RotaryMachineBase;
import mods.officialy.rotary.common.init.RotaryBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityDCEngine extends RotaryMachineBase implements ShaftEmitter {

    private boolean isOn = false;
    private float powerOutput = 0f;
    private float maxPowerOutput = 1024f;
    private float torqueOutput = 4f;
    private float speedOutput = 256f;
    private int ticksSinceTurnedOn = 0;
    private int ticksSinceTurnedOff = 0;

    public BlockEntityDCEngine(BlockPos pos, BlockState state) {
        super(RotaryBlockEntities.DC_ENGINE.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            // Do any additional setup here
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        isOn = tag.getBoolean("IsOn");
        powerOutput = tag.getFloat("PowerOutput");
        torqueOutput = tag.getFloat("TorqueOutput");
        speedOutput = tag.getFloat("SpeedOutput");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("IsOn", isOn);
        tag.putFloat("PowerOutput", powerOutput);
        tag.putFloat("TorqueOutput", torqueOutput);
        tag.putFloat("SpeedOutput", speedOutput);
    }

    @Override
    public float getSpeedOutput() {
        return isOn ? speedOutput : 0f;
    }

    @Override
    public float getTorqueOutput() {
        return isOn ? torqueOutput : 0f;
    }

    @Override
    public float getPowerOutput() {
        return isOn ? powerOutput : 0f;
    }

    @Override
    public void update() {
        if (isOn) {
            if (ticksSinceTurnedOn < 100) {
                // Gradually ramp up power output over 5 seconds
                powerOutput = maxPowerOutput * (ticksSinceTurnedOn / 100f);
                ticksSinceTurnedOn++;
            } else {
                powerOutput = maxPowerOutput;
            }
            ticksSinceTurnedOff = 0;
        } else {
            if (ticksSinceTurnedOff < 100) {
                // Gradually decrease power output over 5 seconds
                powerOutput = maxPowerOutput * (1 - (ticksSinceTurnedOff / 100f));
                ticksSinceTurnedOff++;
            } else {
                powerOutput = 0f;
            }
            ticksSinceTurnedOn = 0;
        }
        setChanged();
    }

    @Override
    public boolean isActive() {
        return isOn;
    }

    // Method to toggle the engine on/off
    public void turnOnOff(boolean on) {
        isOn = on;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        setChanged();
    }
}