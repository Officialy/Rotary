package mods.officialy.rotary.common.init;

import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.common.recipe.BlastFurnaceRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RotaryRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Rotary.MODID);

    public static final RegistryObject<BlastFurnaceRecipe.Serializer> BLAST_FURNACE_SERIALIZER = RECIPE_SERIALIZERS.register("blast_furnace", () -> BlastFurnaceRecipe.Serializer.INSTANCE);

}