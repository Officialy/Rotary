package mods.officialy.rotary.base;

import mods.officialy.rotary.api.machine.Machine;
import mods.officialy.rotary.common.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public abstract class RotaryMachineBlock extends Block implements Machine {


    /**
     * This is the property that determines the possible directions the block is facing. This does contain UP and DOWN,
     * however they are not enabled unless {@link this.hasVerticalPlacement} is set to true.
     */
    public static DirectionProperty FACING = BlockStateProperties.FACING;

    public RotaryMachineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level level, BlockPos pos, Player ep, InteractionHand p_60507_, BlockHitResult p_60508_) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te != null && !level.isClientSide && te instanceof MenuProvider) {
            ep.swing(InteractionHand.MAIN_HAND, true);
            NetworkHooks.openScreen((ServerPlayer) ep, (MenuProvider) te, pos);
            return InteractionResult.SUCCESS;
        }
        return super.use(p_60503_, level, pos, ep, p_60507_, p_60508_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        if (rotatableSides() == 6) {
            return this.defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
        } else {
            return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean hasInventory() {
        return false;
    }

    @Override
    public boolean hasTank() {
        return false;
    }

    @Override
    public boolean isHeatable() {
        return true;
    }

    //Default is 4 sides, aka horizontal only - if you want to allow vertical placement, override this method and return 6
    //This is used in getStateForPlacement
    @Override
    public int rotatableSides() {
        return Mth.clamp(4, 0, 6);
    }
}
