package mods.officialy.rotary.base;

import mods.officialy.rotary.api.machine.Machine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RotaryMachineBase extends BlockEntity implements Machine {
    public RotaryMachineBase(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public boolean isHeatable() {
        return false;
    }

    @Override
    public boolean is4SideRotatable() {
        return false;
    }

    @Override
    public boolean is6SideRotatable() {
        return false;
    }

    @Override
    public boolean is2SideRotatable() {
        return false;
    }

    @Override
    public boolean hasInventory() {
        return false;
    }

    @Override
    public boolean hasTank() {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isActive() {
        return false;
    }
}
