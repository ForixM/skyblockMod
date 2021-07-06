package fr.modcraftmc.skyblock.listeners;

import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SkyBlock.MOD_ID, value = Dist.CLIENT)
public class ClientListener {

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent.Post event){
        Screen screen = event.getGui();
        if (screen instanceof InventoryScreen || screen instanceof CreativeScreen){
            event.addWidget(new Button(5, 20, 50, 20, new StringTextComponent("Skyblock"), b -> {
                screen.onClose();
                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.MAIN, null, GuiCommand.EMPTY));
            }));
        }
    }
}
