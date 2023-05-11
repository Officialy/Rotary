package mods.officialy.rotary.util.voxelshape;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RotatableVoxelShape {

    private final VoxelShape originalShape;
    private final float rotationAngle;
    private final Vector3f rotationAxis;

    public RotatableVoxelShape(VoxelShape originalShape, float rotationAngle, Vector3f rotationAxis) {
        this.originalShape = originalShape;
        this.rotationAngle = rotationAngle;
        this.rotationAxis = rotationAxis;
    }

    public List<AABB> calculateRotatedBoundingBoxes() {
        // Get the original bounding boxes
        List<AABB> originalBoxes = originalShape.toAabbs();

        // Rotate each bounding box
        List<AABB> rotatedBoxes = new ArrayList<>();
        for (AABB box : originalBoxes) {
            rotatedBoxes.add(rotateBoundingBox(box, rotationAngle, rotationAxis));
        }

        return rotatedBoxes;
    }

    private AABB rotateBoundingBox(AABB box, float angle, Vector3f axis) {
        Vector3f min = new Vector3f((float) box.minX, (float) box.minY, (float) box.minZ);
        Vector3f max = new Vector3f((float) box.maxX, (float) box.maxY, (float) box.maxZ);

        // Calculate the center of the bounding box
        Vector3f center = new Vector3f(min).add(max).mul(0.5f);

        // Calculate relative coordinates
        min.sub(center);
        max.sub(center);

        // Create a quaternion for rotation
        Quaternionf rotation = new Quaternionf().fromAxisAngleDeg(axis, angle);

        // Rotate the min and max coordinates
        min.rotate(rotation);
        max.rotate(rotation);

        // Translate back to world coordinates
        min.add(center);
        max.add(center);

        // Create the rotated bounding box
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public VoxelShape createRotatedVoxelShape() {
        List<AABB> rotatedBoxes = calculateRotatedBoundingBoxes();
        VoxelShape result = Shapes.empty();

        for (AABB box : rotatedBoxes) {
            result = Shapes.join(result, Shapes.create(box), BooleanOp.OR);
        }

        return result;
    }

}
