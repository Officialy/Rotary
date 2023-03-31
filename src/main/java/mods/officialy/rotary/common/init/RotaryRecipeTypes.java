package mods.officialy.rotary.common.init;

import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.common.recipe.WorktableRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.extensions.IForgeRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RotaryRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Rotary.MODID);

    public static final RegistryObject<WorktableRecipe.Serializer> WORKTABLE_SERIALIZER = RECIPE_SERIALIZERS.register("worktable", () -> WorktableRecipe.Serializer.INSTANCE);

}