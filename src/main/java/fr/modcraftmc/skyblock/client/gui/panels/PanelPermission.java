package fr.modcraftmc.skyblock.client.gui.panels;

import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.utils.MouseButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Panel;
import com.feed_the_beast.mods.ftbguilibrary.widget.PanelScrollBar;
import com.feed_the_beast.mods.ftbguilibrary.widget.SimpleTextButton;
import com.feed_the_beast.mods.ftbguilibrary.widget.Widget;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.client.gui.GuiSettings;
import fr.modcraftmc.skyblock.client.gui.PERMISSIONS;
import fr.modcraftmc.skyblock.client.gui.widget.ClickableTextButton;
import fr.modcraftmc.skyblock.network.*;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.PermissionSelector;
import fr.modcraftmc.skyblock.network.demands.Request;
import net.minecraft.util.text.StringTextComponent;

import java.util.HashMap;
import java.util.Iterator;

public class PanelPermission extends Panel {
    private PanelScrollBar scrollBar;
    private SimpleTextButton addOfficier, addMember, addGuest, ban, neutral;
    private String selectedPlayer = null;
    private int selectedId = 0;
    private double scrollStep = 0.0;
    private int players = 0;
    private HashMap<String, PERMISSIONS> playerList;

    private Panel memberList;

    public PanelPermission(Panel panel, HashMap<String, PERMISSIONS> playerList, String selectedPlayer, double scrollStep){
        this(panel, playerList);
        this.scrollStep = scrollStep;
        this.selectedPlayer = selectedPlayer;
    }

    public PanelPermission(Panel panel, HashMap<String, PERMISSIONS> playerList) {
        super(panel);
        this.players = playerList.size();
        this.playerList = playerList;
        System.out.println("New iteration of permission panel");
    }

    int count = 0;
    @Override
    public void addWidgets() {
        SkyBlock.LOGGER.info("Adding widgets");
        SkyBlock.LOGGER.info("scrollStep="+scrollStep);
        memberList = new Panel(this) {
            @Override
            public void addWidgets() {
                playerList.forEach((pseudo, permissions) -> {
                    ClickableTextButton player = new ClickableTextButton(this, new StringTextComponent(pseudo), Icon.EMPTY) {
                        @Override
                        public void onClicked(MouseButton mouseButton) {
                            selectedPlayer = this.title.getString();
                        }
                    };
                    count++;
                    switch (permissions){
                        case NEUTRAL:
                            player.setTextColor(Color4I.WHITE);
                            break;
                        case BAN:
                            player.setTextColor(Color4I.RED);
                            break;
                        case GUEST:
                            player.setTextColor(Color4I.GREEN);
                            break;
                        case MEMBER:
                            player.setTextColor(Color4I.LIGHT_BLUE);
                            break;
                        case OFFICIER:
                            player.setTextColor(Color4I.BLUE);
                            break;
                    }
                    if (pseudo.equals(selectedPlayer))
                        player.punched = true;
                    add(player);
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
        memberList.setPosAndSize(0, 0, this.width-10, this.height-60);
        scrollBar = new PanelScrollBar(this, memberList);
        scrollBar.setPosAndSize(this.width-10, 0, 10, this.height-60);
        scrollBar.setMaxValue(players*20);
        scrollBar.setValue(scrollStep);
        scrollBar.setScrollStep(scrollStep);
        System.out.println("scrollStep = " + scrollStep);
        String owner = ((GuiSettings)parent).getIslandInfos().getOwner();
        addMember = new SimpleTextButton(this, new StringTextComponent("Add member"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiSettings guiSettings = (GuiSettings) PanelPermission.this.parent;
                assert selectedPlayer != null;
                guiSettings.getIslandInfos().addMember(selectedPlayer);
                PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDMEMBER, selectedPlayer, owner));
                playerList.replace(selectedPlayer, PERMISSIONS.MEMBER);
                scrollStep = scrollBar.getValue();
                PanelPermission.this.refreshWidgets();
//                if (((GuiSettings)parent).getIslandInfos().getOwner().equalsIgnoreCase(DISPLAY_NAME)){
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDMEMBER, selectedPlayer, null, GuiCommand.EMPTY));
//                } else {
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDMEMBER, ((GuiSettings)parent).getIslandInfos().getOwner(), selectedPlayer, GuiCommand.NOTOWNER));
//                }
//                refresh();
            }
        };
        addGuest = new SimpleTextButton(this, new StringTextComponent("Add Guest"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiSettings guiSettings = (GuiSettings) PanelPermission.this.parent;
                assert selectedPlayer != null;
                guiSettings.getIslandInfos().addGuest(selectedPlayer);
                PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDGUEST, selectedPlayer, owner));
                playerList.replace(selectedPlayer, PERMISSIONS.GUEST);
                scrollStep = scrollBar.getValue();
                PanelPermission.this.refreshWidgets();
//                if (((GuiSettings)parent).getIslandInfos().getOwner().equalsIgnoreCase(DISPLAY_NAME)){
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDGUEST, selectedPlayer, null, GuiCommand.EMPTY));
//                } else {
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDGUEST, ((GuiSettings)parent).getIslandInfos().getOwner(), selectedPlayer, GuiCommand.NOTOWNER));
//                }
//                refresh();
            }
        };
        addOfficier = new SimpleTextButton(this, new StringTextComponent("Add Officier"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiSettings guiSettings = (GuiSettings) PanelPermission.this.parent;
                assert selectedPlayer != null;
                guiSettings.getIslandInfos().addOfficer(selectedPlayer);
                PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDOFFICIER, selectedPlayer, owner));
                playerList.replace(selectedPlayer, PERMISSIONS.OFFICIER);
                scrollStep = scrollBar.getValue();
                PanelPermission.this.refreshWidgets();
//                if (((GuiSettings)parent).getIslandInfos().getOwner().equalsIgnoreCase(DISPLAY_NAME)){
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDOFFICIER, selectedPlayer, null, GuiCommand.EMPTY));
//                } else {
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.ADDOFFICIER, ((GuiSettings)parent).getIslandInfos().getOwner(), selectedPlayer, GuiCommand.NOTOWNER));
//                }
//                refresh();
            }
        };
        ban = new SimpleTextButton(this, new StringTextComponent("Ban"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiSettings guiSettings = (GuiSettings) PanelPermission.this.parent;
                assert selectedPlayer != null;
                guiSettings.getIslandInfos().addBan(selectedPlayer);
                PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.BAN, selectedPlayer, owner));
                playerList.replace(selectedPlayer, PERMISSIONS.BAN);
                scrollStep = scrollBar.getValue();
                PanelPermission.this.refreshWidgets();
//                if (((GuiSettings)parent).getIslandInfos().getOwner().equalsIgnoreCase(DISPLAY_NAME)){
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.BAN, selectedPlayer, null, GuiCommand.EMPTY));
//                } else {
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.BAN, ((GuiSettings)parent).getIslandInfos().getOwner(), selectedPlayer, GuiCommand.NOTOWNER));
//                }
//                refresh();
            }
        };
        neutral = new SimpleTextButton(this, new StringTextComponent("Neutral"), Icon.EMPTY) {
            @Override
            public void onClicked(MouseButton mouseButton) {
                GuiSettings guiSettings = (GuiSettings) PanelPermission.this.parent;
                assert selectedPlayer != null;
                guiSettings.getIslandInfos().setNeutral(selectedPlayer);
                PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.NEUTRAL, selectedPlayer, owner));
                playerList.replace(selectedPlayer, PERMISSIONS.NEUTRAL);
                scrollStep = scrollBar.getValue();
                PanelPermission.this.refreshWidgets();
//                if (((GuiSettings)parent).getIslandInfos().getOwner().equalsIgnoreCase(DISPLAY_NAME)){
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.NEUTRAL, selectedPlayer, null, GuiCommand.EMPTY));
//                } else {
//                    PacketHandler.INSTANCE.sendToServer(new PacketModifyPermission(PermissionSelector.NEUTRAL, ((GuiSettings)parent).getIslandInfos().getOwner(), selectedPlayer, GuiCommand.NOTOWNER));
//                }
//                refresh();
            }
        };

        add(memberList);
        add(scrollBar);
        add(addMember);
        add(addGuest);
        add(addOfficier);
        add(ban);
        add(neutral);
    }

    private void refresh(){
        PacketHandler.INSTANCE.sendToServer(new PacketOpenGUI(Request.SETTINGS, selectedPlayer + ";" + scrollBar.getValue(), GuiCommand.REFRESH_PERMISSION));
    }

    @Override
    public void alignWidgets() {
        int margin = 5;
        addGuest.setPosAndSize(margin, height-60, width/2-margin*2, 20);
        addMember.setPosAndSize(width/2+margin, height-60, width/2-margin*2, 20);
        addOfficier.setPosAndSize(margin, height-35, width/2-margin*2, 20);
        ban.setPosAndSize(width/2+margin, height-35, width/2-margin*2, 20);
        neutral.setPosAndSize(width/2-(width/2-margin*2)/2, height-15, width/2-margin*2, 20);
    }
}
