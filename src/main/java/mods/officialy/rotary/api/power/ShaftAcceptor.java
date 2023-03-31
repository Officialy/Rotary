package mods.officialy.rotary.api.power;

public interface ShaftAcceptor {

        // Method to get the current speed of the machine
        float getSpeed();

        // Method to get the current torque of the machine
        float getTorque();

        // Method to get the current power of the machine
        float getPower();

        // Method to tick the machine (e.g. to process input or output, or to produce power etc.)
        void update();

        // Method to check if the machine is currently active (i.e. producing power)
        boolean isActive();
}