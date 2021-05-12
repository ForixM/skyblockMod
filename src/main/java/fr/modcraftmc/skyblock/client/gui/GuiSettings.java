package fr.modcraftmc.skyblock.client.gui;

import com.feed_the_beast.mods.ftbguilibrary.icon.Icon;
import com.feed_the_beast.mods.ftbguilibrary.widget.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.client.gui.panels.PanelConfig;
import fr.modcraftmc.skyblock.client.gui.panels.PanelInfos;
import fr.modcraftmc.skyblock.client.gui.panels.PanelPermission;
import fr.modcraftmc.skyblock.client.gui.panels.PanelSelection;
import fr.modcraftmc.skyblock.util.IslandInfos;

import java.util.HashMap;

public class GuiSettings extends GuiBase {

    private PanelSelection selection = new PanelSelection(this);

    private PanelInfos infos;
    private PanelPermission permission;
    private PanelConfig panelConfig;
    private Icon icon = Icon.getIcon(SkyBlock.MOD_ID+":textures/gui/skyblock_settings.png");
    private IslandInfos islandInfos;
    private JsonArray playerList;

    public IslandInfos getIslandInfos() {
        return islandInfos;
    }

    public void setMenu(SelectedMenu selectedMenu){
        this.selectedMenu = selectedMenu;
        refreshWidgets();
    }

    @Override
    public void drawBackground(MatrixStack matrixStack, Theme theme, int x, int y, int w, int h) {
        icon.draw(x, y, w, h);
    }

    private TextField title;
    private SelectedMenu selectedMenu;

    public GuiSettings(SelectedMenu selectedMenu){
        selection.setPosAndSize(10, 30, 100, 120);

        title = new TextField(this);
        title.setText("Island Settings");
        title.setPos(30, 14);
        this.selectedMenu = selectedMenu;

        setSize(300, 169);
    }

    public GuiSettings(IslandInfos islandInfos, JsonArray playerList){
        this(islandInfos, playerList, SelectedMenu.INFOS);
    }

    public GuiSettings(IslandInfos islandInfos, JsonArray playerList, SelectedMenu selectedMenu){
        this.islandInfos = islandInfos;
        this.playerList = playerList;
        selection.setPosAndSize(10, 30, 100, 120);

        title = new TextField(this);
        title.setText("Island Settings");
        title.setPos(26, 14);
        this.selectedMenu = selectedMenu;
        setSize(300, 169);
        this.openGui();
    }

//    public GuiSettings(boolean isPublic){
//        this (SelectedMenu.CONFIGURATION);
//        panelConfig = new PanelConfig(this, isPublic);
//        panelConfig.setPosAndSize(140, 15, (posX+width-20)-140, this.height-10);
//        this.openGui();
//    }

//    public GuiSettings(HashMap<String, PERMISSIONS> playerList){
//        this(SelectedMenu.PERMISSION);
//        permission = new PanelPermission(this, playerList);
//        permission.setPosAndSize(140, 15, (posX+width-20)-140, this.height-10);
//        this.openGui();
//    }

    public GuiSettings(HashMap<String, PERMISSIONS> playerList, String selectedPlayer, double scrollStep){
        this(SelectedMenu.PERMISSION);
        permission = new PanelPermission(this, playerList, selectedPlayer, scrollStep);
        permission.setPosAndSize(140, 15, (posX+width-20)-140, this.height-10);
        this.openGui();
    }

//    public GuiSettings(String islandName, String islandDescription){
//        this(SelectedMenu.INFOS);
//        infos = new PanelInfos(this, islandName, islandDescription);
//        infos.setPosAndSize(130, 10, 200, 150);
//        this.openGui();
//    }

    @Override
    public void addWidgets() {
        add(selection);
        add(title);
        switch (selectedMenu) {
            case INFOS:
                infos = new PanelInfos(this, islandInfos.getName(), islandInfos.getDescription());
                infos.setPosAndSize(130, 10, 200, 150);
                add(infos);
                break;
            case PERMISSION:
                System.out.println("islandInfos = " + islandInfos);
                permission = new PanelPermission(this, generatePlayerList(islandInfos.getMembers(), islandInfos.getGuests(), islandInfos.getOfficiers(), islandInfos.getBans()));
                permission.setPosAndSize(140, 15, 140, this.height-10);
                add(permission);
                break;
            case CONFIGURATION:
                panelConfig = new PanelConfig(this, islandInfos.isPublic());
                panelConfig.setPosAndSize(140, 15, (posX+width-20)-140, this.height-10);
                add(panelConfig);
                break;
        }
    }

    private HashMap<String, PERMISSIONS> generatePlayerList(JsonArray memberList, JsonArray guestList, JsonArray officierList, JsonArray banList){
        HashMap<String, PERMISSIONS> players = new HashMap<>();
        mainLoop:
        for (JsonElement player : playerList) {
            if (memberList != null) {
                for (JsonElement member : memberList) {
                    if (member.getAsString().equals(player.getAsString())) {
                        players.put(player.getAsString(), PERMISSIONS.MEMBER);
                        continue mainLoop;
                    }
                }
            }
            if (guestList != null) {
                for (JsonElement guest : guestList) {
                    if (guest.getAsString().equals(player.getAsString())) {
                        players.put(player.getAsString(), PERMISSIONS.GUEST);
                        continue mainLoop;
                    }
                }
            }
            if (officierList != null) {
                for (JsonElement officier : officierList) {
                    if (officier.getAsString().equals(player.getAsString())) {
                        players.put(player.getAsString(), PERMISSIONS.OFFICIER);
                        continue mainLoop;
                    }
                }
            }
            if (banList != null) {
                for (JsonElement ban : banList) {
                    if (ban.getAsString().equals(player.getAsString())) {
                        players.put(player.getAsString(), PERMISSIONS.BAN);
                        continue mainLoop;
                    }
                }
            }
            players.put(player.getAsString(), PERMISSIONS.NEUTRAL);
        }
        return players;
    }
}
