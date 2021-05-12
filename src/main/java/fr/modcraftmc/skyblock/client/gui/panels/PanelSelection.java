package fr.modcraftmc.skyblock.client.gui.panels;

import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Panel;
import com.feed_the_beast.mods.ftbguilibrary.widget.SimpleTextButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Widget;
import fr.modcraftmc.skyblock.client.gui.GuiIslandInfos;
import fr.modcraftmc.skyblock.client.gui.GuiMain;
import fr.modcraftmc.skyblock.client.gui.GuiSettings;
import fr.modcraftmc.skyblock.client.gui.SelectedMenu;
import fr.modcraftmc.skyblock.client.gui.widget.ClickableTextButton;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.Request;
import fr.modcraftmc.skyblock.util.IslandInfos;
import net.minecraft.util.text.StringTextComponent;

import java.util.Iterator;

import static fr.modcraftmc.skyblock.util.Constants.DISPLAY_NAME;

public class PanelSelection extends Panel {

    GuiSettings guiParent;

    public PanelSelection(GuiSettings panel) {
        super(panel);
        this.guiParent = panel;
    }

    @Override
    public void addWidgets() {
        add(new ClickableTextButton(this, new StringTextComponent("Infos"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                //PanelSelection.this.parent.closeGui();
                guiParent.setMenu(SelectedMenu.INFOS);
//                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.INFOS, null, GuiCommand.EMPTY));
            }
        });
        add(new ClickableTextButton(this, new StringTextComponent("Permission"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                //PanelSelection.this.parent.closeGui();
                guiParent.setMenu(SelectedMenu.PERMISSION);
//                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.PERMISSIONS, null, GuiCommand.PERMISSION_BASE));
            }
        });
        add(new ClickableTextButton(this, new StringTextComponent("Configuration"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                //PanelSelection.this.parent.closeGui();
                guiParent.setMenu(SelectedMenu.CONFIGURATION);
//                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.CONFIGURATION, null, GuiCommand.EMPTY));
            }
        });
        add(new SimpleTextButton(this, new StringTextComponent("Back"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                //parent.closeGui();
                if (guiParent.getIslandInfos().getOwner().equalsIgnoreCase(DISPLAY_NAME))
                    new GuiMain(true);
                else
                    new GuiIslandInfos(guiParent.getIslandInfos());
            }
        });
    }

    @Override
    public boolean mousePressed(MouseButton button) {
        Iterator<Widget> widgets = this.widgets.iterator();
        Widget widget;
        while (widgets.hasNext()){
            widget = widgets.next();
            if (widget instanceof ClickableTextButton){
                if (widget.isEnabled() && widget.mousePressed(button)) {
                    depunchAll();
                    ((ClickableTextButton) widget).punched = true;
                }
            }
        }
        return super.mousePressed(button);
    }

    private void depunchAll(){
        Iterator<Widget> widgets = this.widgets.iterator();
        Widget widget;
        while (widgets.hasNext()){
            widget = widgets.next();
            if (widget instanceof ClickableTextButton){
                ((ClickableTextButton) widget).punched = false;
            }
        }
    }

    @Override
    public void alignWidgets() {
        Iterator<Widget> widgets = this.widgets.iterator();
        Widget widget;
        int sep = height / this.widgets.size();
        int y = sep/2-10;
        while (widgets.hasNext()){
            widget = widgets.next();
            widget.setPosAndSize(10, y, 85, 20);
            y+=sep;
        }
    }
}
