package mods.officialy.rotary.common.init;

import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.common.block.entity.BlastFurnaceBlockEntity;
import mods.officialy.rotary.common.block.entity.machine.BlockEntityDCEngine;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RotaryBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Rotary.MODID);

    public static RegistryObject<BlockEntityType<BlastFurnaceBlockEntity>> BLAST_FURNACE = BLOCK_ENTITIES.register("blast_furnace", () -> BlockEntityType.Builder.of(BlastFurnaceBlockEntity::new, RotaryBlocks.BLAST_FURNACE.get()).build(null));
    public static RegistryObject<BlockEntityType<BlockEntityDCEngine>> DC_ENGINE = BLOCK_ENTITIES.register("dc_engine", () -> BlockEntityType.Builder.of(BlockEntityDCEngine::new, RotaryBlocks.DC_ENGINE.get()).build(null));


}