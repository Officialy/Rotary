package mods.officialy.rotary.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BlastFurnaceRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;
    //The temperature required to craft
    private final float operatingTemperature;
    //The experience points you get from crafting
    private final float experience;
    //The higher the tier, the longer it takes to craft
    private final float timeMultiplier;
    private boolean needsAdditives = false;

    public BlastFurnaceRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack output, float temperature, float experience, float timeMultiplier, boolean needsAdditives) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
        this.operatingTemperature = temperature;
        this.experience = experience;
        this.timeMultiplier = timeMultiplier;
        this.needsAdditives = needsAdditives;
    }

    public float getOperatingTemperature() {
        return operatingTemperature;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level world) {
        // Check if all additives match - Ignores if needsAdditives is false
        if (needsAdditives) {
            for (int i = 0; i < 3; i++) {
                ItemStack additiveStack = inv.getItem(i);
                if (!additiveStack.isEmpty() && i < this.ingredients.size() && !this.ingredients.get(i).test(additiveStack)) {
                    return false;
                }
            }
        }

        // Check if all recipe items match
        for (int i = 0; i < 9; i++) {
            ItemStack recipeStack = inv.getItem(i + 3); // Skip the first three slots for additives
            if (!recipeStack.isEmpty() && i < this.ingredients.size() && (!this.ingredients.get(i).test(recipeStack))) {
                return false;
            }
        }

        // All ingredients match
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public float getExperience() {
        return experience;
    }

    public float getTimeMultiplier() {
        return timeMultiplier;
    }

    public boolean needsAdditives() {
        return needsAdditives;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<BlastFurnaceRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "blast_furnace";
    }

    public static class Serializer implements RecipeSerializer<BlastFurnaceRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public BlastFurnaceRecipe fromJson(ResourceLocation id, JsonObject json) {
            Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = ShapedRecipe.dissolvePattern(astring, map, i, j);
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));


            float temperature = json.get("temperature").getAsFloat();
            float experience = json.get("experience").getAsFloat();
            float timeMultiplier = json.get("timeMultiplier").getAsFloat();
            boolean needsAdditives = json.get("needsAdditives").getAsBoolean();

            return new BlastFurnaceRecipe(id, nonnulllist, output, temperature, experience, timeMultiplier, needsAdditives);
        }

        @Override
        public @Nullable BlastFurnaceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf byteBuf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(7, Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(byteBuf));
            }
            ItemStack output = byteBuf.readItem();
            float temperature = byteBuf.readFloat();
            float experience = byteBuf.readFloat();
            float timeMultiplier = byteBuf.readFloat();
            boolean needsAdditives = byteBuf.readBoolean();
            return new BlastFurnaceRecipe(id, inputs, output, temperature, experience, timeMultiplier, needsAdditives);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BlastFurnaceRecipe recipe) {
            buffer.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItemStack(recipe.getResultItem(RegistryAccess.EMPTY), true);
            buffer.writeFloat(recipe.getOperatingTemperature());
            buffer.writeFloat(recipe.getExperience());
            buffer.writeFloat(recipe.getTimeMultiplier());
            buffer.writeBoolean(recipe.needsAdditives());
        }
    }

}
