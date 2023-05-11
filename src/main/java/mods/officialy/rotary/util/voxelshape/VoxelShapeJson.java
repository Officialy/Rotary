package mods.officialy.rotary.util.voxelshape;

import java.util.List;
import java.util.Map;

public class VoxelShapeJson {

    public String credit;
    public List<Element> elements;

    public static class Element {
        public String name;
        public List<Float> from;
        public List<Float> to;
        public Rotation rotation;
        public int color;
        public Map<String, Face> faces;
    }

    public static class Rotation {
        public float angle;
        public String axis;
        public List<Float> origin;
    }

    public static class Face {
        public List<Float> uv;
        public String texture;
        public Integer rotation;
    }
}