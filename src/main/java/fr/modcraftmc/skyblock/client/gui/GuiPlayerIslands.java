package fr.modcraftmc.skyblock.client.gui;

import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.*;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.client.gui.widget.ClickableTextButton;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.util.text.StringTextComponent;

import java.util.Iterator;
import java.util.Map;

import static fr.modcraftmc.skyblock.util.Constants.DISPLAY_NAME;

public class GuiPlayerIslands extends GuiBase {

    private Map<String, Boolean> islands;
    private Panel playerIslands;
    private PanelScrollBar scrollBar;
    private SimpleTextButton back;

    public GuiPlayerIslands(Map<String, Boolean> islands){
        this.islands = islands;
        playerIslands = new Panel(this) {
            @Override
            public void addWidgets() {
                islands.forEach((islandName, isPublic) -> {
                    ClickableTextButton island = new ClickableTextButton(playerIslands, new StringTextComponent(islandName), Icon.EMPTY) {
                        @Override
                        public void onClicked(MouseButton mouseButton) {
                            GuiPlayerIslands.this.closeGui();
                            PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.ISLANDINFOS, islandName, GuiCommand.NOTOWNER));
                        }
                    };
                    add(island);
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
                            this.setOffset(false);
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
                int y = 0;
                while (widgets.hasNext()){
                    widget = widgets.next();
                    widget.setPosAndSize(0, y, this.width, 20);
                    y+=20;
                }
            }
        };
        this.openGui();
    }

    @Override
    public void addWidgets() {
        playerIslands.setPosAndSize(10, 10, this.width-20, this.height);
        add(playerIslands);
        scrollBar = new PanelScrollBar(this, playerIslands);
        scrollBar.setPosAndSize(this.width-20, 10, 10, this.height-10);
        scrollBar.setMaxValue(islands.size()*20);
        scrollBar.setValue(0);
        scrollBar.setScrollStep(1);
        add(scrollBar);
        back = new SimpleTextButton(this, new StringTextComponent("Back"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                closeGui();
                new GuiMain(true);
            }
        };
        back.setPosAndSize(170, 10, 50, 20);
        add(back);
    }


}
