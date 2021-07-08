package fr.modcraftmc.skyblock.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.client.gui.*;
import fr.modcraftmc.skyblock.database.Connector;
import fr.modcraftmc.skyblock.network.demands.GuiCommand;
import fr.modcraftmc.skyblock.network.demands.Request;
import fr.modcraftmc.skyblock.util.Constants;
import fr.modcraftmc.skyblock.util.IslandInfos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketDirection;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PacketOpenGUI implements PacketBasic{

    private Request request;
    private String message;
    private GuiCommand guiCommand;
    
    private final String SEPARATOR = ";";

    public PacketOpenGUI(PacketBuffer buf){
        request = buf.readEnum(Request.class);
        message = buf.readUtf(Constants.MAX_STRING_SIZE);
        guiCommand = buf.readEnum(GuiCommand.class);
    }

    public void toBytes(PacketBuffer buf){
        buf.writeEnum(request);
        buf.writeUtf(message);
        buf.writeEnum(guiCommand);
    }

    public PacketOpenGUI(Request request, String message, GuiCommand guiCommand){
        this.request = request;
        this.guiCommand = guiCommand;
        if (message != null)
            this.message = message;
        else
            this.message = "";
    }

    private HashMap<String, PERMISSIONS> generatePlayerList(JsonArray playerList, JsonArray memberList, JsonArray guestList, JsonArray officierList, JsonArray banList){
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

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity playerEntity = ctx.get().getSender();
            if (ctx.get().getNetworkManager().getDirection() == PacketDirection.CLIENTBOUND) {
                String[] splittedMessage = message.split(SEPARATOR);
                switch (request) {
                    case MAIN:
                        if (message.equals("true"))
                            new GuiMain(true);
                        else if (message.equals("false"))
                            new GuiMain(false);
                        break;
                    case INFOS:
                        openSettings(splittedMessage, SelectedMenu.INFOS);
                        break;
                    case PERMISSIONS:
                        openSettings(splittedMessage, SelectedMenu.PERMISSION);
                        break;
                    case CONFIGURATION:
                        openSettings(splittedMessage, SelectedMenu.CONFIGURATION);
                        break;
                    case PLAYERISLANDS:
                        Map<String, Boolean> islandPublics = new HashMap<>();
                        for (String island : splittedMessage){
                            String[] islandInfo = island.split("=");
                            islandPublics.put(islandInfo[0], islandInfo[1].equals("true"));
                        }
                        new GuiPlayerIslands(islandPublics);
                        break;
                    case ISLANDINFOS:
                        IslandInfos islandInfos = new IslandInfos(splittedMessage[0])
                                .setName(splittedMessage[1])
                                .setDescription(splittedMessage[2])
                                .setBans((JsonArray) new JsonParser().parse(splittedMessage[3]))
                                .setGuests((JsonArray) new JsonParser().parse(splittedMessage[4]))
                                .setMembers((JsonArray) new JsonParser().parse(splittedMessage[5]))
                                .setOfficiers((JsonArray) new JsonParser().parse(splittedMessage[6]))
                                .setPublic(splittedMessage[7].equals("true"))
                                .setSize(Integer.parseInt(splittedMessage[8]))
                                .setSpawn(new Vector3d(0, 100, 0));
                        new GuiIslandInfos(islandInfos);
                        break;
                    case SETTINGS:
                        openSettings(splittedMessage);
                        break;
                    case ERROR:
                        new GuiError(message);
                }
            } else if (ctx.get().getNetworkManager().getDirection() == PacketDirection.SERVERBOUND) {
                String pseudo = playerEntity.getDisplayName().getString();
                switch (request) {
                    case MAIN:
                        message = String.valueOf(SkyBlock.config.haveIsland(pseudo));
                        break;
                    case INFOS:
                        message = prepareIslandInfosMessage(pseudo);
                        break;
                    case PERMISSIONS:
                        message = prepareIslandInfosMessage(pseudo);
                        break;
                    case CONFIGURATION:
                        message = prepareIslandInfosMessageWithPerm(message, pseudo);

                        break;
                    case PLAYERISLANDS:
                        Map<String, Boolean> islandPublics = SkyBlock.config.getIslandPublics();
                        StringBuilder messageBuilder = new StringBuilder();
                        islandPublics.forEach((islandName, isPublic) -> {
                            messageBuilder.append(islandName+"="+isPublic+SEPARATOR);
                        });
                        messageBuilder.deleteCharAt(messageBuilder.length()-1);
                        message = messageBuilder.toString();
                        break;
                    case ISLANDINFOS:
                        message = prepareIslandInfosMessage(SkyBlock.config.getOwner(message));
                        break;
                    case SETTINGS:
                        message = prepareIslandInfosMessageWithPerm(message, pseudo);
                        break;
                }
                PacketHandler.INSTANCE.sendTo(new PacketOpenGUI(request, message, guiCommand), playerEntity.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private String prepareIslandInfosMessage(String owner){
        StringBuilder islandInfos = new StringBuilder();
        islandInfos.append(owner).append(SEPARATOR);
        islandInfos.append(SkyBlock.config.getIslandName(owner)).append(SEPARATOR);
        islandInfos.append(SkyBlock.config.getIslandDescription(owner)).append(SEPARATOR);
        islandInfos.append(Connector.getBanneds(owner)).append(SEPARATOR);
        islandInfos.append(Connector.getGuests(owner)).append(SEPARATOR);
        islandInfos.append(Connector.getMembers(owner)).append(SEPARATOR);
        islandInfos.append(Connector.getOfficers(owner)).append(SEPARATOR);
        islandInfos.append(SkyBlock.config.isPublic(owner)).append(SEPARATOR);
        islandInfos.append(SkyBlock.config.getIslandSize(owner)).append(SEPARATOR);
        islandInfos.append(SkyBlock.config.getSpawnLocation(owner)).append(SEPARATOR);
        islandInfos.append(Connector.getPlayerList());
        return islandInfos.toString();
    }

    private String prepareIslandInfosMessageWithPerm(String player, String sender){
        if (player.equals(sender)){
            return prepareIslandInfosMessage(sender);
        } else {
            JsonArray officers = Connector.getOfficers(player);
            for (JsonElement officier : officers){
                if (officier.getAsString().equalsIgnoreCase(sender)){
                    return prepareIslandInfosMessage(player);
                }
            }
        }
        request = Request.ERROR;
        return "You can't modify this island settings because you arn't officer in this island. Please contact the island owner.";
    }

    private void openSettings(String[] splittedMessage){
        openSettings(splittedMessage, SelectedMenu.INFOS);
    }

    private void openSettings(String[] splittedMessage, SelectedMenu selectedMenu){
        IslandInfos islandInfoss = new IslandInfos(splittedMessage[0])
                .setName(splittedMessage[1])
                .setDescription(splittedMessage[2])
                .setBans((JsonArray) new JsonParser().parse(splittedMessage[3]))
                .setGuests((JsonArray) new JsonParser().parse(splittedMessage[4]))
                .setMembers((JsonArray) new JsonParser().parse(splittedMessage[5]))
                .setOfficiers((JsonArray) new JsonParser().parse(splittedMessage[6]))
                .setPublic(splittedMessage[7].equals("true"))
                .setSize(Integer.parseInt(splittedMessage[8]))
                .setSpawn(new Vector3d(0, 100, 0));
        new GuiSettings(islandInfoss, (JsonArray)new JsonParser().parse(splittedMessage[10]), selectedMenu);
    }
}
