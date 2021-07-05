package fr.modcraftmc.skyblock.client.gui.panels;

import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Panel;
import com.feed_the_beast.mods.ftbguilibrary.widget.SimpleTextButton;
import fr.modcraftmc.skyblock.client.gui.GuiSettings;
import fr.modcraftmc.skyblock.client.gui.widget.ClickableTextButton;
import fr.modcraftmc.skyblock.network.*;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.util.text.StringTextComponent;

import static fr.modcraftmc.skyblock.util.Constants.DISPLAY_NAME;

public class PanelConfig extends Panel {

    private SimpleTextButton setSpawn;
    private ClickableTextButton setPublic;

    private boolean publicIsland = false;

    public PanelConfig(Panel panel, boolean publicIsland) {
        super(panel);
        this.publicIsland = publicIsland;
        setSpawn = new SimpleTextButton(this, new StringTextComponent("Set spawn"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiSettings guiSettings = (GuiSettings)PanelConfig.this.parent;
                String owner = guiSettings.getIslandInfos().getOwner();
                PacketHandler.INSTANCE.sendToServer(new PacketSetSpawn(owner));
            }
        };
        setPublic = new ClickableTextButton(this, new StringTextComponent("Public Island"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                try {
                    GuiSettings guiSettings = (GuiSettings)PanelConfig.this.parent;
                    String owner = guiSettings.getIslandInfos().getOwner();
                    PacketHandler.INSTANCE.sendToServer(new PacketSetPublicIsland(!publicIsland, owner));
                    guiSettings.getIslandInfos().setPublic(!publicIsland);
                    PanelConfig.this.publicIsland = !publicIsland;
                    guiSettings.refreshWidgets();
                } catch (Exception e){
                    System.out.println("parent = " + parent);
                    e.printStackTrace();
                }
            }
        };

        if (publicIsland)
            setPublic.setTextColor(Color4I.GREEN);
        else
            setPublic.setTextColor(Color4I.RED);
    }

    @Override
    public void addWidgets() {
        add(setSpawn);
        add(setPublic);
    }

    @Override
    public void alignWidgets() {
        setSpawn.setPosAndSize(10, 10, 100, 20);
        setPublic.setPosAndSize(10, 40, 100, 20);
    }
}
