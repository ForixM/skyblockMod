package fr.modcraftmc.skyblock.client;

import fr.modcraftmc.skyblock.SkyBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkyBlock.MOD_ID, value = Dist.CLIENT)
public class EventHandler {

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event){
//        Screen screen = event.getGui();
//        if (screen instanceof MultiplayerScreen){
//            event.addWidget(new Button(5, 5, 100, 20, "Relogin", b -> {
//                openAuthenticationScreen(screen);
//            }));
//        }
    }



}
