package mods.officialy.rotary.api.machine;

public interface Machine {

    // Method to check if the machine is currently active (i.e. producing power)
    boolean isActive();

    // Method to enable the machine to have an inventory handler
    boolean hasInventory();

    // Method to enable the machine to have a fluid handler
    boolean hasTank();

    // Method to enable the machine to be heatable
    boolean isHeatable();

    // Method to enable the machine to be horizontally and/or vertically rotatable (i.e. Grinder (4), Shaft (6), Bedrock Breaker (6))
    // 4 = Horizontal, 6 = Vertical
    int rotatableSides();
}
