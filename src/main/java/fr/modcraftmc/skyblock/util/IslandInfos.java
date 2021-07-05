package fr.modcraftmc.skyblock.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Arrays;

public class IslandInfos {
    private String name;
    private String owner;
    private String description;
    private JsonArray officiers;
    private JsonArray members;
    private JsonArray guests;
    private JsonArray bans;
    private Vector3d spawn;
    private int size;
    private boolean isPublic;

    public IslandInfos(String owner){
        this.owner = owner;
    }

    public IslandInfos setName(String name){
        this.name = name;
        return this;
    }

    public IslandInfos setDescription(String description){
        this.description = description;
        return this;
    }

    public void addOfficer(String player){
        setNeutral(player);
        this.officiers.add(player);
    }

    public void removeOfficer(String player){
        for (JsonElement element : officiers){
            if (element.getAsString().equalsIgnoreCase(player)){
                officiers.remove(element);
                return;
            }
        }
    }

    public IslandInfos setOfficiers(JsonArray officiers){
        this.officiers = officiers;
        return this;
    }

    public void addMember(String player){
        setNeutral(player);
        members.add(player);
    }

    public void removeMember(String player){
        for (JsonElement element : members){
            if (element.getAsString().equalsIgnoreCase(player)){
                members.remove(element);
                return;
            }
        }
    }

    public IslandInfos setMembers(JsonArray members){
        this.members = members;
        return this;
    }

    public void addGuest(String player){
        setNeutral(player);
        guests.add(player);
    }

    public void removeGuest(String player) {
        for (JsonElement element : guests) {
            if (element.getAsString().equalsIgnoreCase(player)) {
                guests.remove(element);
                return;
            }
        }
    }

    public IslandInfos setGuests(JsonArray guests){
        this.guests = guests;
        return this;
    }

    public void addBan(String player){
        setNeutral(player);
        bans.add(player);
    }

    public void removeBan(String player){
        for (JsonElement element : bans){
            if (element.getAsString().equalsIgnoreCase(player)){
                bans.remove(element);
                return;
            }
        }
    }

    public IslandInfos setBans(JsonArray bans){
        this.bans = bans;
        return this;
    }

    public void setNeutral(String player){
        removeOfficer(player);
        removeGuest(player);
        removeMember(player);
        removeBan(player);
    }

    public IslandInfos setSpawn(Vector3d spawn){
        this.spawn = spawn;
        return this;
    }

    public IslandInfos setSize(int size){
        this.size = size;
        return this;
    }

    public IslandInfos setPublic(boolean isPublic){
        this.isPublic = isPublic;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public JsonArray getOfficiers() {
        return officiers;
    }

    public JsonArray getMembers() {
        return members;
    }

    public JsonArray getGuests() {
        return guests;
    }

    public JsonArray getBans() {
        return bans;
    }

    public Vector3d getSpawn() {
        return spawn;
    }

    public int getSize() {
        return size;
    }

    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return "IslandInfos{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", description='" + description + '\'' +
                ", officiers=" + officiers +
                ", members=" + members +
                ", guests=" + guests +
                ", bans=" + bans +
                ", spawn=" + spawn +
                ", size=" + size +
                ", isPublic=" + isPublic +
                '}';
    }
}
