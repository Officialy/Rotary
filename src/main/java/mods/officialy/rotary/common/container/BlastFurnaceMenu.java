package mods.officialy.rotary.common.container;

import mods.officialy.rotary.common.block.entity.BlastFurnaceBlockEntity;
import mods.officialy.rotary.common.init.RotaryBlocks;
import mods.officialy.rotary.common.init.RotaryMenus;
import mods.officialy.rotary.util.ResultSlotItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class BlastFurnaceMenu extends AbstractContainerMenu {

    public final BlastFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public BlastFurnaceMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public BlastFurnaceMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(RotaryMenus.BLAST_FURNACE_MENU.get(), pContainerId);
        checkContainerSize(inv, 4);
        blockEntity = ((BlastFurnaceBlockEntity) entity);
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            int id = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.addSlot(new SlotItemHandler(handler, id++, 62 + j * 18, 17 + i * 18));  // Crafting grid
                }
            }
            this.addSlot(new SlotItemHandler(handler, id++, 33, 17));  // Additive
            this.addSlot(new SlotItemHandler(handler, id++, 33, 35));  // Additive
            this.addSlot(new SlotItemHandler(handler, id++, 33, 53));  // Additive
        });
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent(handler -> {
            int id = 0;
            this.addSlot(new ResultSlotItemHandler(handler, id++, 128, 17));  // Output
            this.addSlot(new ResultSlotItemHandler(handler, id++, 128, 35));  // Output
            this.addSlot(new ResultSlotItemHandler(handler, id, 128, 53));  // Output
        });
        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 26; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY; //todo
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, RotaryBlocks.BLAST_FURNACE.get());
    }

    private void addPlayerInventory(Inventory inv) {
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(inv, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inv, j, 8 + j * 18, 142));
        }
    }

}