package fr.modcraftmc.skyblock.client.gui;

import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.GuiBase;
import com.feed_the_beast.mods.ftbguilibrary.widget.SimpleTextButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Theme;
import com.feed_the_beast.mods.ftbguilibrary.widget.Widget;
import com.mojang.blaze3d.matrix.MatrixStack;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.network.PacketTeleportToIsland;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Iterator;
import java.util.Locale;

import static fr.modcraftmc.skyblock.util.Constants.DISPLAY_NAME;

public class GuiMain extends GuiBase {

    private final boolean haveIsland;

    public GuiMain(boolean haveIsland){
        this.haveIsland = haveIsland;
        setSize(256, 256);
        this.openGui();
    }

    @Override
    public void addWidgets() {
        if (haveIsland){
            add(new SimpleTextButton(this, new StringTextComponent("Teleport to Island"), Icon.EMPTY) {
                @Override
                public void onClicked(MouseButton mouseButton) {
                    PacketHandler.INSTANCE.sendToServer(new PacketTeleportToIsland(DISPLAY_NAME));
                    GuiMain.this.closeGui();
                }
            });
            add(new SimpleTextButton(this, new StringTextComponent("Settings"), Icon.EMPTY) {
                @Override
                public void onClicked(MouseButton mouseButton) {
                    PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.SETTINGS, DISPLAY_NAME, GuiCommand.EMPTY));
                    closeGui();
                }
            });

        } else {
            add(new SimpleTextButton(this, new StringTextComponent("Create Island"), Icon.EMPTY) {
                @Override
                public void onClicked(MouseButton mouseButton) {
                    PacketHandler.INSTANCE.sendToServer(new PacketTeleportToIsland(DISPLAY_NAME));
                    GuiMain.this.closeGui();
                }
            });
        }
        add(new SimpleTextButton(this, new StringTextComponent("Player islands"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.PLAYERISLANDS, null, GuiCommand.EMPTY));
                closeGui();
            }
        });
        add(new SimpleTextButton(this, new StringTextComponent("Close"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiMain.this.closeGui(false);
            }
        });
    }

    private final Icon icon = Icon.getIcon(SkyBlock.MOD_ID+":textures/gui/skyblock_menu.png");

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        icon.draw(x, y, w, h);
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
        Iterator<Widget> widgets = this.widgets.iterator();
        Widget widget;
        int y = 85;
        while (widgets.hasNext()){
            widget = widgets.next();
            widget.setPosAndSize(40, y, width-80, 20);
            y+=28;
        }
    }
}
