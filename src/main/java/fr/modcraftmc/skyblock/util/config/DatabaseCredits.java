package fr.modcraftmc.skyblock.util.config;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;

public class DatabaseCredits {
    public static ForgeConfigSpec.ConfigValue<String> url;
    public static ForgeConfigSpec.ConfigValue<String> username;
    public static ForgeConfigSpec.ConfigValue<String> password;

    public static void init(ForgeConfigSpec.Builder server){
        server.comment("Database creditentials");

        url = server.comment("Put url after //").define("database.url", "jdbc:mysql://");
        username = server.comment("database username").define("database.username", "");
        password = server.comment("database password").define("database.password", "");
    }
}
