package mods.officialy.rotary.api.machine;

public interface Machine {

    // Method to tick the machine (e.g. to process input or output, or to produce power etc.)
    void update();

    // Method to check if the machine is currently active (i.e. producing power)
    boolean isActive();

    // Method to enable the machine to have an inventory handler
    boolean hasInventory();

    // Method to enable the machine to have a fluid handler
    boolean hasTank();

    // Method to enable the machine to be heatable
    boolean isHeatable();

    // Method to enable the machine to be horizontally rotatable (i.e. Worktable, Sawmill)
    boolean is4SideRotatable();

    // Method to enable the machine to be horizontally and vertically rotatable (i.e. Shaft, Bedrock Breaker)
    boolean is6SideRotatable();

    // Method to enable the machine to be only vertically rotatable (i.e. todo idk what to put here rn)
    boolean is2SideRotatable();
}
