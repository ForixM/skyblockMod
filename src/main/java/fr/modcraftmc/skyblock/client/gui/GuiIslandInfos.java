package fr.modcraftmc.skyblock.client.gui;

import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.client.gui.widget.ClickableTextButton;
import fr.modcraftmc.skyblock.network.PacketHandler;
import fr.modcraftmc.skyblock.network.PacketOpenGUI;
import fr.modcraftmc.skyblock.network.PacketTeleportToIsland;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.Request;
import fr.modcraftmc.skyblock.util.IslandInfos;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

import java.util.Iterator;

import static fr.modcraftmc.skyblock.util.Constants.DISPLAY_NAME;

public class GuiIslandInfos extends GuiBase {

    private final IslandInfos islandInfos;
    private final TextField name;
    private final TextField owner;
    private final TextField description;

    private final TextField guestHeader;
    private final TextField memberHeader;
    private final TextField officerHeader;
    private final TextField bansHeader;

    private ClickableTextButton teleportToIsland;
    private ClickableTextButton manageIsland;
    private ClickableTextButton back;

    private final Panel guests;
    private final Panel members;
    private final Panel officers;
    private final Panel bans;

    public GuiIslandInfos(IslandInfos islandInfos){
        this.islandInfos = islandInfos;
        guests = new Panel(this) {
            @Override
            public void addWidgets() {
                islandInfos.getGuests().forEach((player) -> {
                    System.out.println("guest="+player);
                    add(new TextField(guests).setText(player.getAsString()));
                });
            }

            @Override
            public void alignWidgets() {
                Iterator<Widget> widgets = this.widgets.iterator();
                Widget widget;
                int y = 0;
                while (widgets.hasNext()){
                    widget = widgets.next();
                    widget.setPosAndSize(0, y, this.width, 20);
                    y+=10;
                }
            }
        };

        members = new Panel(this) {
            @Override
            public void addWidgets() {
                islandInfos.getMembers().forEach((player) -> add(new TextField(members).setText(player.getAsString())));
            }

            @Override
            public void alignWidgets() {
                Iterator<Widget> widgets = this.widgets.iterator();
                Widget widget;
                int y = 0;
                while (widgets.hasNext()){
                    widget = widgets.next();
                    widget.setPosAndSize(0, y, this.width, 20);
                    y+=10;
                }
            }
        };

        officers = new Panel(this) {
            @Override
            public void addWidgets() {
                islandInfos.getOfficiers().forEach((player) -> add(new TextField(officers).setText(player.getAsString())));
            }

            @Override
            public void alignWidgets() {
                Iterator<Widget> widgets = this.widgets.iterator();
                Widget widget;
                int y = 0;
                while (widgets.hasNext()){
                    widget = widgets.next();
                    widget.setPosAndSize(0, y, this.width, 20);
                    y+=10;
                }
            }
        };

        bans = new Panel(this) {
            @Override
            public void addWidgets() {
                islandInfos.getBans().forEach((player) -> add(new TextField(bans).setText(player.getAsString())));
            }

            @Override
            public void alignWidgets() {
                Iterator<Widget> widgets = this.widgets.iterator();
                Widget widget;
                int y = 0;
                while (widgets.hasNext()){
                    widget = widgets.next();
                    widget.setPosAndSize(0, y, this.width, 20);
                    y+=10;
                }
            }
        };

        this.name = new TextField(this).setText("Name: "+islandInfos.getName());
        this.owner = new TextField(this).setText("Owner: "+islandInfos.getOwner());
        this.description = new TextField(this).setText("Description: "+islandInfos.getDescription()).setMaxWidth(100);
        this.guestHeader = new TextField(this).setText("Guests");
        this.memberHeader = new TextField(this).setText("Members");
        this.officerHeader = new TextField(this).setText("Officers");
        this.bansHeader = new TextField(this).setText("Banned");
        this.setSize(300, 200);
        this.openGui();
    }

    @Override
    public void alignWidgets() {
        name.setPosAndSize(10, 10, 100, 15);
        owner.setPosAndSize(10, 25, 100, 15);
        description.setPos(10, 40);

        int step = this.width/4;
        guestHeader.setPos(20, 100);
        memberHeader.setPos(step+20, 100);
        officerHeader.setPos(step*2+20, 100);
        bansHeader.setPos(step*3+20, 100);

        teleportToIsland.setPosAndSize(185, 10, 105, 20);
        manageIsland.setPosAndSize(185, 35, 105, 20);
        back.setPosAndSize(185, 60, 105, 20);

        super.alignWidgets();
    }

    @Override
    public void addWidgets() {
        int step = this.width/4;
        guests.setPosAndSize(10, 110, 50, this.height-100);
        members.setPosAndSize(step+10, 110, 50, this.height-100);
        officers.setPosAndSize(step*2+10, 110, 50, this.height-100);
        bans.setPosAndSize(step*3+10, 110, 50, this.height-100);

        teleportToIsland = new ClickableTextButton(this, new StringTextComponent("Teleport To Island"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                super.onClicked(mouseButton);
                if (enabled) {
                    assert Minecraft.getInstance().player != null;
                    PacketHandler.INSTANCE.sendToServer(new PacketTeleportToIsland(islandInfos.getOwner()));
                    closeGui();
                }
            }
        };
        teleportToIsland.enabled = false;
        if (islandInfos.isPublic())
            teleportToIsland.enabled = true;
        else {
            islandInfos.getGuests().forEach((guest) -> {
                if (guest.getAsString().equalsIgnoreCase(DISPLAY_NAME)) {
                    teleportToIsland.enabled = true;
                }
            });
            islandInfos.getMembers().forEach((member) -> {
                if (member.getAsString().equalsIgnoreCase(DISPLAY_NAME)) {
                    teleportToIsland.enabled = true;
                }
            });
            islandInfos.getOfficiers().forEach((officer) -> {
                if (officer.getAsString().equalsIgnoreCase(DISPLAY_NAME)) {
                    teleportToIsland.enabled = true;
                }
            });
            if (islandInfos.getOwner().equalsIgnoreCase(DISPLAY_NAME)){
                teleportToIsland.enabled = true;
            }
        }

        manageIsland = new ClickableTextButton(this, new StringTextComponent("Manage Island"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                super.onClicked(mouseButton);
                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.SETTINGS, islandInfos.getOwner(), GuiCommand.NOTOWNER));
                closeGui();
            }
        };
        manageIsland.enabled = false;
        if (islandInfos.getOwner().equalsIgnoreCase(DISPLAY_NAME)){
            manageIsland.enabled = true;
        } else {
            islandInfos.getOfficiers().forEach((officer) -> {
                if (officer.getAsString().equalsIgnoreCase(DISPLAY_NAME)) {
                    manageIsland.enabled = true;
                }
            });
        }

        back = new ClickableTextButton(this, new StringTextComponent("Back"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                super.onClicked(mouseButton);
                GuiIslandInfos.this.closeGui();
                PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.PLAYERISLANDS, null, GuiCommand.EMPTY));
            }
        };

        add(name);
        add(owner);
        add(description);
        add(guestHeader);
        add(memberHeader);
        add(officerHeader);
        add(bansHeader);
        add(guests);
        add(members);
        add(officers);
        add(bans);
        add(teleportToIsland);
        add(manageIsland);
        add(back);
    }

    private final Icon icon = Icon.getIcon(SkyBlock.MOD_ID+":textures/gui/island_info.png");

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        icon.draw(x, y, w, h);
    }
}
