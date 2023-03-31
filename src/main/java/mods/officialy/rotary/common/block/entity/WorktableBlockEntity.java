package mods.officialy.rotary.common.block.entity;

import mods.officialy.rotary.api.machine.Heatable;
import mods.officialy.rotary.base.RotaryMachineBase;
import mods.officialy.rotary.common.container.WorktableMenu;
import mods.officialy.rotary.common.init.RotaryBlockEntities;
import mods.officialy.rotary.common.recipe.WorktableRecipe;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WorktableBlockEntity extends RotaryMachineBase implements MenuProvider, Tickable, Heatable {

    // The Inventory, 12 for crafting
    private final ItemStackHandler itemHandler = new ItemStackHandler(12) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    //The output inventory, 3 for output
    private final ItemStackHandler outputInv = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 120; // Default is 6 seconds, multiplied by tier
    private float temperature = 20.0f;
    private static float minimumOperatingTemperature = 0f;
    private final float maximumTemperature = 2100.0f;

    public WorktableBlockEntity(BlockPos pos, BlockState state) {
        super(RotaryBlockEntities.WORKTABLE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
            if (hasRecipe(this) && temperature >= minimumOperatingTemperature) {
                progress++;
                Level level = getLevel();
                SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
                for (int i = 0; i < itemHandler.getSlots(); i++) {
                    inventory.setItem(i, itemHandler.getStackInSlot(i));
                }

                Optional<WorktableRecipe> match = level.getRecipeManager().getRecipeFor(WorktableRecipe.Type.INSTANCE, inventory, level);

                if (match.isPresent()) {
                    // Craft the recipe and output the result
                    ItemStack output = match.get().getResultItem(RegistryAccess.EMPTY).copy();
                    itemHandler.extractItem(0, match.get().getIngredients().size(), false);
                    for (int i = 1; i < 10; i++) {
                        itemHandler.extractItem(i, 1, false);
                    }
                    itemHandler.insertItem(9, output, false);
                }
            } else {
                progress = 0;
                setChanged();
            }
        }
    }

    private static boolean hasRecipe(WorktableBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return false;
        }
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        SimpleContainer outputInventory = new SimpleContainer(blockEntity.outputInv.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        Optional<WorktableRecipe> recipe = level.getRecipeManager().getRecipeFor(WorktableRecipe.Type.INSTANCE, inventory, level);
        if (recipe.isPresent()) {
            minimumOperatingTemperature = recipe.get().getOperatingTemperature();
        }
        return recipe.isPresent() && canInsertAmountIntoOutputSlot(outputInventory) && canInsertItemIntoOutputSlot(outputInventory, recipe.get().getResultItem(RegistryAccess.EMPTY));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(3).getItem() == output.getItem() || inventory.getItem(3).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(3).getMaxStackSize() > inventory.getItem(3).getCount();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("itemInventory"));
        outputInv.deserializeNBT(tag.getCompound("outputInventory"));
        temperature = tag.getFloat("temperature");
        progress = tag.getInt("progress");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("outputInventory", outputInv.serializeNBT());
        tag.putFloat("temperature", temperature);
        tag.putFloat("progress", progress);
    }

    @Override
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    @Override
    public float getTemperature() {
        return temperature;
    }

    @Override
    public float getMinimumOperatingTemperature() {
        return minimumOperatingTemperature;
    }

    @Override
    public float getMaximumTemperature() {
        return maximumTemperature;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new WorktableMenu(id, inv, this, this.data);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Worktable");
    }
}
