package fr.modcraftmc.skyblock.util.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import fr.modcraftmc.skyblock.SkyBlock;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class ConfigHandler {
    private static final ForgeConfigSpec.Builder server_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec server_config;

    static {
        DatabaseCredits.init(server_builder);
        server_config = server_builder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path){
        SkyBlock.LOGGER.info("Loading config: "+path);
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        SkyBlock.LOGGER.info("Loaded config: "+path);
        config.setConfig(file);
    }
}
