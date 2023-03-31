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

public class WorktableRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;
    //The temperature required to craft
    private final float operatingTemperature;
    //The experience points you get from crafting
    private final float experience;
    //The higher the tier, the longer it takes to craft
    private final float timeMultiplier;

    public WorktableRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack output, float temperature, float experience, float timeMultiplier) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
        this.operatingTemperature = temperature;
        this.experience = experience;
        this.timeMultiplier = timeMultiplier;
    }

    public float getOperatingTemperature() {
        return operatingTemperature;
    }

    @Override
    public boolean matches(SimpleContainer inv, Level p_44003_) {
        for (int i = 0; i <= ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ItemStack itemStack = inv.getItem(i);
            if (ingredient.test(itemStack)) {
                return true;
            }
        }
        return false;
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<WorktableRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "worktable";
    }

    public static class Serializer implements RecipeSerializer<WorktableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public WorktableRecipe fromJson(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(7, Ingredient.EMPTY);
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredients").get(i)));
            }

            float temperature = json.get("temperature").getAsFloat();
            float experience = json.get("experience").getAsFloat();
            float timeMultiplier = json.get("timeMultiplier").getAsFloat();
            return new WorktableRecipe(id, inputs, output, temperature, experience, timeMultiplier);
        }

        @Override
        public @Nullable WorktableRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf byteBuf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(7, Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(byteBuf));
            }
            ItemStack output = byteBuf.readItem();
            float temperature = byteBuf.readFloat();
            float experience = byteBuf.readFloat();
            float timeMultiplier = byteBuf.readFloat();
            return new WorktableRecipe(id, inputs, output, temperature, experience, timeMultiplier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, WorktableRecipe recipe) {
            buffer.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItemStack(recipe.getResultItem(RegistryAccess.EMPTY), true);
            buffer.writeFloat(recipe.getOperatingTemperature());
            buffer.writeFloat(recipe.getExperience());
            buffer.writeFloat(recipe.getTimeMultiplier());
        }
    }

}
