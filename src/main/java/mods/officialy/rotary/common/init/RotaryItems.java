package mods.officialy.rotary.common.init;

import mods.officialy.rotary.Rotary;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RotaryItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Rotary.MODID);

    public static final RegistryObject<Item> HSLA_STEEL = ITEMS.register("hsla_steel", () -> new Item(new Item.Properties()));

}