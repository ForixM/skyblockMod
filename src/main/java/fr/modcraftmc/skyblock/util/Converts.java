package fr.modcraftmc.skyblock.util;

import net.minecraft.util.math.vector.Vector3d;

public class Converts {

    public static Vector3d StringToVec3d(String value){
        StringBuilder builder = new StringBuilder().append(value);
        builder.deleteCharAt(0);
        builder.deleteCharAt(builder.length()-1);
        for (int i = 0; i < builder.length(); i++){
            if (builder.charAt(i) == ' '){
                builder.deleteCharAt(i);
            }
        }
        String[] loc = builder.toString().split(",");
        return new Vector3d(Float.parseFloat(loc[0]) , Float.parseFloat(loc[1]), Float.parseFloat(loc[2]));
    }
}
