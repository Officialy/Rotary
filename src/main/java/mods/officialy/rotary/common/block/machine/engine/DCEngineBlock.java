package mods.officialy.rotary.common.block.machine.engine;

import mods.officialy.rotary.base.HeatableMachineBlock;
import mods.officialy.rotary.common.block.entity.machine.BlockEntityDCEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DCEngineBlock extends HeatableMachineBlock implements EntityBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();

    public DCEngineBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BlockEntityDCEngine(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ((world, pos, state, blockEntity) -> ((BlockEntityDCEngine) blockEntity).update());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block p_60512_, BlockPos pos2, boolean p_60514_) {
        super.neighborChanged(state, level, pos2, p_60512_, pos, p_60514_);
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BlockEntityDCEngine) {
                BlockEntityDCEngine engine = (BlockEntityDCEngine) blockEntity;
                boolean flag = state.getValue(POWERED);
                if (level.hasNeighborSignal(pos)) {
                    level.setBlock(pos, state.setValue(POWERED, true), 2);
                    engine.turnOnOff(true);
                } else if (flag) {
                    level.setBlock(pos, state.setValue(POWERED, false), 2);
                    engine.turnOnOff(false);
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        super.getStateForPlacement(context);
        return this.defaultBlockState().setValue(POWERED, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
    }
/*    @Override
    public VoxelShape getShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_) {
        return rotateShape(Direction.NORTH, p_60479_.getValue(FACING), shape);
    }*/

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        return SHAPES.computeIfAbsent(from, f -> {
            VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

            int times = (to.ordinal() - from.get2DDataValue() + 4) % 4;
            for (int i = 0; i < times; i++) {
                buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
                buffer[0] = buffer[1];
                buffer[1] = Shapes.empty();
            }

            return buffer[0];
        });
    }
}