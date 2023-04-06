package mods.officialy.rotary.client;

import mods.officialy.rotary.Rotary;
import mods.officialy.rotary.client.screen.BlastFurnaceScreen;
import mods.officialy.rotary.common.init.RotaryMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Rotary.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        menuScreens();
    }

    public static void menuScreens() {
        Minecraft minecraft = Minecraft.getInstance();
        //MenuScreens.registerFactory(RegistryHandler.MY_CONTAINER.get(), MyContainerScreen::new);
        MenuScreens.register(RotaryMenus.BLAST_FURNACE_MENU.get(), BlastFurnaceScreen::new);
    }


}
