package mods.officialy.rotary.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.common.block.entity.BlastFurnaceBlockEntity;
import mods.officialy.rotary.common.container.BlastFurnaceMenu;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.client.gui.ScreenUtils;

public class BlastFurnaceScreen extends AbstractContainerScreen<BlastFurnaceMenu> {

    private BlastFurnaceBlockEntity blast;
    private BlastFurnaceMenu container;

    public BlastFurnaceScreen(BlastFurnaceMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        blast = container.blockEntity;
        this.container = container;
    }

    @Override
    protected void init() {
        super.init();

        int w = (width - imageWidth) / 2;
        int h = (height - imageHeight) / 2;

        Component tip = blast.leaveLastItem ? Component.literal("Leave one item") : Component.literal("Consume all items");

        addRenderableWidget(new ImageButton(w + 32, h,16, 16,0, 0, 0, new ResourceLocation("textures/block/dirt.png"), 16, 16, this::onPressButton, tip));
    }

    private void onPressButton(Button button) {
        var tag = blast.getUpdateTag();
        Rotary.LOGGER.info("Button pressed " + "tag is: " + tag);
        tag.putBoolean("leaveLastItem", !blast.leaveLastItem);
        Rotary.LOGGER.info("It has been changed to " + tag);
        blast.handleUpdateTag(tag);
        blast.setChanged();
    }

    @Override
    protected void renderBg(PoseStack stack, float p_97788_, int p_97789_, int p_97790_) {
        this.renderBackground(stack);
        int j = (width - imageWidth) / 2;
        int k = (height - imageHeight) / 2;
        int i1 = container.getScaledProgress() * 24 / 100;
        int i2 = (int) blast.getTemperature() * 70 / 200;
        int c = 0;

        if (blast.getTemperature() >= 1000) {
            c = 1;
        }

        //Background texture
//        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/block/stone_bricks.png"));//new ResourceLocation(Rotary.MODID, "textures/screen/blast_furnace.png"));
//        ScreenUtils.drawTexturedModalRect(stack, j, k, 0, 0, imageWidth, imageHeight, 0);
//        Gui.drawCenteredString(stack, font, blast.getTemperature() + "C", j + 16, k + 14, 17 + c);
//        Gui.drawCenteredString(stack, font, blast.progress + "%", j + 164, k + 64,  4210752);

        //Progress bar
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/block/cyan_wool.png"));
        ScreenUtils.drawTexturedModalRect(stack, j + 119, k + 34, 176, 14, i1 + 1, 16, 0);

        //Heat bar
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/block/lava_still.png"));
        ScreenUtils.drawTexturedModalRect(stack, j + 11, k + 70 - i2, 176, 86 - i2, 10, i2, 0);

     /*   for (int i = 0; i < container.slots.size(); i++) {
            Slot s = container.slots.get(i);
            if (s.hasItem() && s.getItem().getCount() > 1 && blast.leaveLastItem) {
                Gui.fill(stack, j + s.x, k + s.y, j + s.x + 16, k + s.y + 16, 0x50000000);
            }
        }*/

        for (int i = 0; i < container.slots.size(); i++) {
            Slot s = container.slots.get(i);
            Gui.fill(stack, j + s.x, k + s.y, j + s.x + 16, k + s.y + 16, 0x50000000);
        }
    }
}
