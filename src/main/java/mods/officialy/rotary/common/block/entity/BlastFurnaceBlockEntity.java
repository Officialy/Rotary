package mods.officialy.rotary.common.block.entity;

import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.api.machine.Heatable;
import mods.officialy.rotary.base.RotaryMachineBase;
import mods.officialy.rotary.common.container.BlastFurnaceMenu;
import mods.officialy.rotary.common.init.RotaryBlockEntities;
import mods.officialy.rotary.common.recipe.BlastFurnaceRecipe;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlastFurnaceBlockEntity extends RotaryMachineBase implements MenuProvider, Tickable, Heatable {

    // The Inventory, last 3 for additives the first 9 for crafting
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
    private final LazyOptional<IItemHandler> lazyOutputInv = LazyOptional.of(() -> outputInv);
    protected final ContainerData data;
    public int progress = 0;
    private int maxProgress = 120; // Default is 6 seconds, multiplied by tier
    private float temperature = 20.0f;
    private static float minimumOperatingTemperature = 0f;
    private final float maximumTemperature = 2100.0f;
    public boolean leaveLastItem = false;

    public BlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(RotaryBlockEntities.BLAST_FURNACE.get(), pos, state);
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
        if (hasRecipe(this) && temperature >= minimumOperatingTemperature) {
            progress++;
            Rotary.LOGGER.info("Progress: " + progress);
            Level level = getLevel();
            SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                inventory.setItem(i, itemHandler.getStackInSlot(i));
            }

            Optional<BlastFurnaceRecipe> match = level.getRecipeManager().getRecipeFor(BlastFurnaceRecipe.Type.INSTANCE, inventory, level);

            if (match.isPresent()) {
                ItemStack output = match.get().getResultItem(RegistryAccess.EMPTY).copy();
                if (progress >= maxProgress) {
                    if (leaveLastItem) {
                        boolean canCraft = true; // set to false if any slot has less than 2 items
                        for (int i = 0; i < 10; i++) { // check first 10 slots
                            if (itemHandler.getStackInSlot(i).getCount() < 2) {
                                canCraft = false;
                                break; // no need to check other slots if one slot has less than 2 items
                            }
                        }
                        if (canCraft) {
                            // Craft the recipe and output the result
                            itemHandler.extractItem(0, match.get().getIngredients().size(), false);
                            for (int i = 1; i < 10; i++) {
                                itemHandler.extractItem(i, 1, false);
                            }
                            outputInv.insertItem(1, output, false);
                        }
                    } else {
                        // Craft the recipe and output the result
                        itemHandler.extractItem(0, match.get().getIngredients().size(), false);
                        for (int i = 1; i < 10; i++) {
                            itemHandler.extractItem(i, 1, false);
                        }
                        outputInv.insertItem(1, output, false);
                    }
                }
            }
        } else {
            progress = 0;
            setChanged();
        }
    }

    private static boolean hasRecipe(BlastFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return false;
        }
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        SimpleContainer outputInventory = new SimpleContainer(blockEntity.outputInv.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        Optional<BlastFurnaceRecipe> recipe = level.getRecipeManager().getRecipeFor(BlastFurnaceRecipe.Type.INSTANCE, inventory, level);
        recipe.ifPresent(blastFurnaceRecipe -> minimumOperatingTemperature = blastFurnaceRecipe.getOperatingTemperature());
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
        leaveLastItem = tag.getBoolean("leaveLastItem");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.put("outputInventory", outputInv.serializeNBT());
        tag.putFloat("temperature", temperature);
        tag.putFloat("progress", progress);
        tag.putBoolean("leaveLastItem", leaveLastItem);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, blockEntity -> this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        var tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
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

            return side == Direction.DOWN ? this.lazyOutputInv.cast() : this.lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new BlastFurnaceMenu(id, inv, this, this.data);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Blast Furnace");
    }
}
