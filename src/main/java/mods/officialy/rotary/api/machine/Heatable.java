package mods.officialy.rotary.api.machine;

public interface Heatable {

    // Method to set the current temperature of the object
    void setTemperature(float temperature);

    // Method to get the current temperature of the object
    float getTemperature();

    // Method to get the minimum temperature that the object requires
    float getMinimumOperatingTemperature();

    // Method to get the maximum temperature that the object can tolerate
    float getMaximumTemperature();

}
