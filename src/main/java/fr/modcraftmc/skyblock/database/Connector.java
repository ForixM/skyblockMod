package fr.modcraftmc.skyblock.database;

import com.google.gson.*;
import fr.modcraftmc.skyblock.SkyBlock;
import fr.modcraftmc.skyblock.util.config.DatabaseCredits;
import net.minecraft.util.math.vector.Vector3d;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Connector {

    private Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;
    private static JsonParser parser = new JsonParser();

    private String url = DatabaseCredits.url.get();
    private String user = DatabaseCredits.username.get();
    private String password = DatabaseCredits.password.get();


    private static boolean connected = false;


    public static void main(String[] args) {
        new Connector();
    }

    public Connector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connect();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC driver not found !");
        }

    }

    public void connect(){
        try {
            SkyBlock.LOGGER.info("[Modcraft Skyblock] Connecting to database");
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            SkyBlock.LOGGER.info("[Modcraft Skyblock] Connection to database established");
            connected = true;
            createIslandsDatabase();
        } catch (SQLException throwables) {
            SkyBlock.LOGGER.info("[Modcraft Skyblock] Couldn't connect to database");
            throwables.printStackTrace();
        }
    }

    private void createIslandsDatabase(){
        DatabaseMetaData md = null;
        try {
            md = connection.getMetaData();
            String[] urlParts = url.split("/");
            resultSet = md.getTables(urlParts[urlParts.length-1], null, "%", null);
            boolean islandsExist = false;
            boolean playersExist = false;
            while (resultSet.next()){
                if (resultSet.getString("TABLE_NAME").equals("islands"))
                    islandsExist = true;
                if (resultSet.getString("TABLE_NAME").equals("players"))
                    playersExist = true;
            }
            if (!islandsExist) {
                statement.executeUpdate("CREATE TABLE islands (id INT NOT NULL AUTO_INCREMENT , name VARCHAR(255) NOT NULL , owner VARCHAR(255) NOT NULL , description VARCHAR(255) NOT NULL , officers JSON NOT NULL DEFAULT '[]' , members JSON NOT NULL DEFAULT '[]' , guests JSON NOT NULL DEFAULT '[]' , bans JSON NOT NULL DEFAULT '[]' , spawn JSON NOT NULL DEFAULT '[0, 100, 0]' , size INT NOT NULL DEFAULT '128' , public TINYINT NOT NULL DEFAULT '0' , PRIMARY KEY (id));");
                SkyBlock.LOGGER.info("Islands table created");
            }
            if (!playersExist){
                statement.executeUpdate("CREATE TABLE players (pseudo VARCHAR(255));");
                SkyBlock.LOGGER.info("Players table created");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean isConnected(){
        return connected;
    }

    public void createIsland(String owner, String description, String name){
        try {
            statement.executeUpdate("INSERT INTO islands (name, owner, description) VALUES ('"+name+"', '"+owner+"', '"+description+"')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createIsland(String owner){
        try {
            statement.executeUpdate("INSERT INTO islands (id, name, owner, description) VALUES ("+findId()+", '"+owner+" island', '"+owner+"', 'Lost island in the middle of the sky')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setName(String owner, String name){
        try {
            name = name.replace("'", "''");
            statement.executeUpdate("UPDATE islands SET name = '"+name+"' WHERE owner = '"+owner.toLowerCase()+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setDescription(String owner, String description) {
        try {
            description = description.replace("'", "''");
            statement.executeUpdate("UPDATE islands SET description = '"+description+"' WHERE owner = '"+owner.toLowerCase()+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setSpawnLocation(String owner, JsonArray spawnLocation){
        try {
            statement.executeUpdate("UPDATE islands SET spawn = '"+spawnLocation.toString()+"' WHERE owner = '"+owner.toLowerCase().toLowerCase()+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setOwner(String oldOwner, String newOwner){
        try {
            statement.executeUpdate("UPDATE islands SET owner = '"+newOwner+"' WHERE owner = '"+oldOwner+"'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setPublic(String owner, boolean openIsland){
        SkyBlock.LOGGER.debug("openIsland="+openIsland);
        SkyBlock.LOGGER.debug("where owner is="+owner);
        try{
            if (openIsland)
                statement.executeUpdate("UPDATE islands SET public = '1' WHERE owner = '"+owner.toLowerCase().toLowerCase()+"'");
            else
                statement.executeUpdate("UPDATE islands SET public = '0' WHERE owner = '"+owner.toLowerCase().toLowerCase()+"'");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void addBan(String owner, String banned){
        try {
            resultSet = statement.executeQuery("SELECT owner, bans FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray banList = (JsonArray) parser.parse(resultSet.getString("bans"));
                    for (JsonElement element : banList){
                        if (element.getAsString().equalsIgnoreCase(banned))
                            return;
                    }
                    banList.add(banned);
                    statement.executeUpdate("UPDATE islands SET bans = '"+banList.toString()+"' WHERE owner = '"+owner.toLowerCase()+"'");
                    removeGuest(owner, banned);
                    removeMember(owner, banned);
                    removeOfficier(owner, banned);
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addGuest(String owner, String newGuest){
        try {
            resultSet = statement.executeQuery("SELECT owner, guests FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray guestList = (JsonArray) parser.parse(resultSet.getString("guests"));
                    for (JsonElement element : guestList){
                        if (element.getAsString().equalsIgnoreCase(newGuest))
                            return;
                    }
                    guestList.add(newGuest);
                    statement.executeUpdate("UPDATE islands SET guests = '"+guestList.toString()+"' WHERE owner = '"+owner.toLowerCase()+"'");
                    removeMember(owner, newGuest);
                    removeOfficier(owner, newGuest);
                    removeBan(owner, newGuest);
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addMember(String owner, String newMember){
        try {
            resultSet = statement.executeQuery("SELECT owner, members FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray memberList = (JsonArray) parser.parse(resultSet.getString("members"));
                    for (JsonElement element : memberList){
                        if (element.getAsString().equalsIgnoreCase(newMember))
                            return;
                    }
                    memberList.add(newMember);
                    statement.executeUpdate("UPDATE islands SET members = '"+memberList.toString()+"' WHERE owner = '"+owner.toLowerCase()+"'");
                    SkyBlock.LOGGER.info("MEMBER UPDATED");
                    removeGuest(owner, newMember);
                    removeOfficier(owner, newMember);
                    removeBan(owner, newMember);
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addOfficier(String owner, String newOfficier){
        try {
            resultSet = statement.executeQuery("SELECT owner, officers FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray officierList = (JsonArray) parser.parse(resultSet.getString("officers"));
                    for (JsonElement element : officierList){
                        if (element.getAsString().equalsIgnoreCase(newOfficier)) {
                            return;
                        }
                    }
                    officierList.add(newOfficier);
                    statement.executeUpdate("UPDATE islands SET officers = '"+officierList.toString()+"' WHERE owner = '"+owner.toLowerCase().toLowerCase()+"'");
                    removeMember(owner, newOfficier);
                    removeGuest(owner, newOfficier);
                    removeBan(owner, newOfficier);
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setNeutral(String owner, String neutralPlayer){
        removeBan(owner, neutralPlayer);
        removeGuest(owner, neutralPlayer);
        removeMember(owner, neutralPlayer);
        removeOfficier(owner, neutralPlayer);
    }

    public void removeBan(String owner, String removedBan){
        try {
            resultSet = statement.executeQuery("SELECT owner, bans FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray banList = (JsonArray) parser.parse(resultSet.getString("bans"));
                    banList.remove(new JsonPrimitive(removedBan));
                    statement.executeUpdate("UPDATE islands SET bans = '"+banList.toString()+"' WHERE owner = '"+owner.toLowerCase()+"'");
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeGuest(String owner, String removedGuest){
        try {
            resultSet = statement.executeQuery("SELECT owner, guests FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray guestList = (JsonArray) parser.parse(resultSet.getString("guests"));
                    guestList.remove(new JsonPrimitive(removedGuest));
                    statement.executeUpdate("UPDATE islands SET guests = '"+guestList.toString()+"' WHERE owner = '"+owner.toLowerCase()+"'");
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeMember(String owner, String removedMember){
        try {
            resultSet = statement.executeQuery("SELECT owner, members FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray memberList = (JsonArray) parser.parse(resultSet.getString("members"));
                    memberList.remove(new JsonPrimitive(removedMember));
                    statement.executeUpdate("UPDATE islands SET members = '"+memberList.toString()+"' WHERE owner = '"+owner.toLowerCase()+"'");
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeOfficier(String owner, String removedOfficier){
        try {
            resultSet = statement.executeQuery("SELECT owner, officers FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray officierList = (JsonArray) parser.parse(resultSet.getString("officers"));
                    officierList.remove(new JsonPrimitive(removedOfficier));
                    statement.executeUpdate("UPDATE islands SET officers = '"+officierList.toString()+"' WHERE owner = '"+owner.toLowerCase()+"'");
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void newPlayer(String pseudo){
        try {
            statement.executeUpdate("INSERT INTO players (pseudo) VALUES ('"+pseudo+"')");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static JsonArray getPlayerList(){
        try {
            resultSet = statement.executeQuery("SELECT pseudo FROM players");
            JsonArray players = new JsonArray();
            while (resultSet.next()) {
                players.add(parser.parse(resultSet.getString("pseudo")));
            }
            return players;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static JsonArray getGuests(String owner){
        try {
            resultSet = statement.executeQuery("SELECT guests, owner FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray parser = (JsonArray) new JsonParser().parse(resultSet.getString("guests"));
                    return parser;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static JsonArray getMembers(String owner){
        try {
            resultSet = statement.executeQuery("SELECT members, owner FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray parser = (JsonArray) new JsonParser().parse(resultSet.getString("members"));
                    return parser;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static JsonArray getOfficers(String owner){
        try {
            resultSet = statement.executeQuery("SELECT officers, owner FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray parser = (JsonArray) new JsonParser().parse(resultSet.getString("officers"));
                    return parser;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static JsonArray getBanneds(String owner){
        try {
            resultSet = statement.executeQuery("SELECT bans, owner FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray parser = (JsonArray) new JsonParser().parse(resultSet.getString("bans"));
                    return parser;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Vector3d getSpawnLocation(String owner){
        try {
            resultSet = statement.executeQuery("SELECT spawn, owner FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    JsonArray spawnLocation = (JsonArray) new JsonParser().parse(resultSet.getString("spawn"));
                    return new Vector3d(spawnLocation.get(0).getAsDouble(), spawnLocation.get(1).getAsDouble(), spawnLocation.get(2).getAsDouble());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public int getIslandSize(String owner){
        try {
            resultSet = statement.executeQuery("SELECT owner, size FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    return resultSet.getInt("size");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public Map<String, Boolean> getIslandPublics(){
        try {
            resultSet = statement.executeQuery("SELECT name, public FROM islands");
            Map<String, Boolean> islandPublics = new HashMap<>();
            while (resultSet.next()){
                islandPublics.put(resultSet.getString("name"), resultSet.getInt("public") == 1);
            }
            return islandPublics;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isPublic(String owner){
        try {
            resultSet = statement.executeQuery("SELECT owner, public FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    int result = resultSet.getInt("public");
                    SkyBlock.LOGGER.debug("result="+result);
                    if (result == 0) {
                        SkyBlock.LOGGER.debug("false");
                        return false;
                    }else if (result == 1) {
                        SkyBlock.LOGGER.debug("true");
                        return true;
                    }
                    SkyBlock.LOGGER.error("il y a un petit problème");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public String getIslandName(String owner){
        try {
            resultSet = statement.executeQuery("SELECT owner, name FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    return resultSet.getString("name");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getIslandDescription(String owner){
        try {
            resultSet = statement.executeQuery("SELECT owner, description FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(owner)){
                    return resultSet.getString("description");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getOwner(String islandName){
        try {
            resultSet = statement.executeQuery("SELECT owner, name FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("name").equalsIgnoreCase(islandName)){
                    return resultSet.getString("owner");
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean haveIsland(String player){
        try {
            resultSet = statement.executeQuery("SELECT owner FROM islands");
            while (resultSet.next()){
                if (resultSet.getString("owner").equalsIgnoreCase(player)){
                    return true;
                }
            }
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean isNewPlayer(String player){
        try {
            resultSet = statement.executeQuery("SELECT pseudo FROM players");
            while (resultSet.next()){
                if (resultSet.getString("pseudo").equalsIgnoreCase(player)){
                    return false;
                }
            }
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void setDefaultSpawnLocation(Vector3d location){
        JsonArray test = new JsonArray();
        test.add(5);
        test.add(123);
        test.add(666);
        System.out.println(test);
    }

    public boolean isGuest(String owner, String player){
        JsonArray guests = getGuests(owner);
        for (JsonElement guest : guests){
            if (guest.getAsString().equalsIgnoreCase(player))
                return true;
        }
        return false;
    }

    public boolean isMember(String owner, String player){
        JsonArray members = getMembers(owner);
        for (JsonElement member : members){
            if (member.getAsString().equalsIgnoreCase(player))
                return true;
        }
        return false;
    }

    public boolean isOfficier(String owner, String player){
        JsonArray officiers = getOfficers(owner);
        for (JsonElement officier : officiers){
            if (officier.getAsString().equalsIgnoreCase(player))
                return true;
        }
        return false;
    }

    public boolean isBanned(String owner, String player){
        JsonArray banneds = getBanneds(owner);
        for (JsonElement banned : banneds){
            if (banned.getAsString().equalsIgnoreCase(player))
                return true;
        }
        return false;
    }

    private int findId(){
        try {
            resultSet = statement.executeQuery("SELECT id FROM islands");
            int id = 1;
            while (resultSet.next()){
                if (id == resultSet.getInt("id")){
                    id++;
                } else {
                    return id;
                }
            }
            return id;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }
}
