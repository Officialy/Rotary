package mods.officialy.rotary.common.init;

import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.common.block.BlastFurnaceBlock;
import mods.officialy.rotary.common.block.machine.engine.DCEngineBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RotaryBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Rotary.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Rotary.MODID);

    public static final RegistryObject<Block> BLAST_FURNACE = register("blast_furnace", () -> new BlastFurnaceBlock(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Block> DC_ENGINE = register("dc_engine", () -> new DCEngineBlock(BlockBehaviour.Properties.of(Material.METAL)));


    public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> object = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(object.get(), new Item.Properties()));
        return object;
    }

}