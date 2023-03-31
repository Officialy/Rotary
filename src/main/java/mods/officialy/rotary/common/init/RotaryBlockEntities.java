package mods.officialy.rotary.common.init;

import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.common.block.entity.WorktableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RotaryBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Rotary.MODID);

    public static RegistryObject<BlockEntityType<WorktableBlockEntity>> WORKTABLE = BLOCK_ENTITIES.register("worktable", () -> BlockEntityType.Builder.of(WorktableBlockEntity::new, RotaryBlocks.WORKTABLE.get()).build(null));

}