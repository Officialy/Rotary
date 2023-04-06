package mods.officialy.rotary;

import com.mojang.logging.LogUtils;
import mods.officialy.rotary.common.init.RotaryBlockEntities;
import mods.officialy.rotary.common.init.RotaryBlocks;
import mods.officialy.rotary.common.init.RotaryMenus;
import mods.officialy.rotary.common.init.RotaryItems;
import mods.officialy.rotary.common.init.RotaryRecipeTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Rotary.MODID)
public class Rotary {

    public static final String MODID = "rotary";
    public static final Logger LOGGER = LogUtils.getLogger();


    public Rotary() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);

        RotaryBlocks.BLOCKS.register(modEventBus);
        RotaryBlocks.ITEMS.register(modEventBus);
        RotaryItems.ITEMS.register(modEventBus);
        RotaryBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        RotaryRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        RotaryMenus.MENUS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(CreativeModeTabEvent.BuildContents event) {
//        if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS)
//            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}