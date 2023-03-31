package mods.officialy.rotary.api.machine;

public interface HeatSource {

    // Enum to define pre-set heat values
    enum HeatLevel {
        CRYO(-196),
        FREEZING(-1),
        AMBIENT(13.9f),
        WARM(30),
        HOT(45),
        BLAZING(100),
        FUSION(10000000);

        private final float temperature;

        HeatLevel(float temperature) {
            this.temperature = temperature;
        }

        public float getTemperature() {
            return temperature;
        }

        public boolean isHot() {
            return temperature >= 45;
        }

        public boolean isCold() {
            return temperature <= 0;
        }
    }

    // Method to get the heat output of the source
    float getHeatOutput();

    // Method to get the heat level of the source
    HeatLevel getHeatLevel();

}
