package mods.officialy.rotary.api.power;

public interface ShaftEmitter {

        // Method to get the current speed being emitted from the machine
        float getSpeedOutput();

        // Method to get the current torque emitted from the machine
        float getTorqueOutput();

        // Method to get the current power output of the machine, in Watts. Just a convenience method for getSpeedOutput() * getTorqueOutput().
        // Can be overridden if necessary for some other magic machinery.
        default float getPowerOutput(){
                return getSpeedOutput() * getTorqueOutput();
        }

        // Method to tick the machine (e.g. to process input or output, or to produce power etc.)
        void update();

        // Method to check if the machine is currently active (i.e. producing power)
        boolean isActive();
}