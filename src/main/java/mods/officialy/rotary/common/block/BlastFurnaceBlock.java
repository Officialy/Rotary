package mods.officialy.rotary.common.block;

import com.google.gson.Gson;
import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.base.HeatableMachineBlock;
import mods.officialy.rotary.common.block.entity.BlastFurnaceBlockEntity;
import mods.officialy.rotary.util.voxelshape.RotatableVoxelShape;
import mods.officialy.rotary.util.voxelshape.VoxelShapeJson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BlastFurnaceBlock extends HeatableMachineBlock implements EntityBlock {

    public static BooleanProperty LIT = BooleanProperty.create("lit");
    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
    private static final Map<Direction, VoxelShape> SHAPE = new HashMap<>();

    public BlastFurnaceBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LIT);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BlastFurnaceBlockEntity(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ((world, pos, state, blockEntity) -> ((BlastFurnaceBlockEntity) blockEntity).tick());
    }

    public float rotationAngle = 14.0f; // Angle in degrees
    public Vector3f rotationAxis = new Vector3f(1, 0, 0); // Rotate around the Y-axis
    public static Path jsonFilePath = Paths.get("C:\\Max\\Modding\\Max's Mods\\Rotary\\src\\main\\resources\\assets\\rotary\\models\\block\\blast_furnace.json");


    // Initialize the map with default shapes for each possible key (0)
    static {
        for (Direction dir : Direction.values()) {
            try {
                SHAPE.put(dir, createVoxelShapeFromJson(jsonFilePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_) {
        Direction facing = p_60479_.getValue(FACING);

        // Use the already computed shape
        VoxelShape defaultShape = SHAPE.get(facing);

        // If the shape has not been computed yet, create it and store it in the SHAPES map
        if (!SHAPES.containsKey(facing)) {
            RotatableVoxelShape customShape = new RotatableVoxelShape(defaultShape, rotationAngle, rotationAxis);
            SHAPES.put(facing, customShape.createRotatedVoxelShape());
        }

        // Retrieve the final shape
        return SHAPES.get(facing);
    }


    public static VoxelShape createVoxelShapeFromJson(Path jsonFilePath) throws IOException {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(jsonFilePath.toFile())) {
            // Parse the JSON file into a VoxelShapeJson object
            VoxelShapeJson voxelShapeJson = gson.fromJson(reader, VoxelShapeJson.class);

            // Convert the JSON object into a VoxelShape
            VoxelShape originalShape = Shapes.empty();
            for (VoxelShapeJson.Element element : voxelShapeJson.elements) {

                VoxelShape partShape = Shapes.create(
                        element.from.get(0) / 16, element.from.get(1) / 16, element.from.get(2) / 16,
                        element.to.get(0) / 16, element.to.get(1) / 16, element.to.get(2) / 16);

                if (element.rotation != null && element.rotation.angle != 0 && element.rotation.axis != null) {
                    Vector3f rotationAxis = axisToVector3f(element.rotation.axis);
                    RotatableVoxelShape customShape = new RotatableVoxelShape(partShape, element.rotation.angle, rotationAxis);
                    partShape = customShape.createRotatedVoxelShape();
                }

                originalShape = Shapes.join(originalShape, partShape, BooleanOp.OR);
            }

            return originalShape;
        }
    }

    private static Vector3f axisToVector3f(String axis) {
        return switch (axis) {
            case "x" -> new Vector3f(1, 0, 0);
            case "y" -> new Vector3f(0, 1, 0);
            case "z" -> new Vector3f(0, 0, 1);
            default -> new Vector3f(0, 1, 0);
        };
    }

}