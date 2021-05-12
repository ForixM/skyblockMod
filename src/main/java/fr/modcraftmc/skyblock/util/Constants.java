package fr.modcraftmc.skyblock.util;


import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Constants {
    public static final int MAX_STRING_SIZE = 32767;

    public static String DISPLAY_NAME = Minecraft.getInstance().player.getDisplayName().getString();
}
