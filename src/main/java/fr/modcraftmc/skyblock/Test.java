package fr.modcraftmc.skyblock;

import fr.modcraftmc.skyblock.util.config.DatabaseCredits;
import net.minecraft.util.math.vector.Vector3d;

import java.sql.*;
import java.util.Arrays;

public class Test {
//    public static void main(String[] args) {
//        Vector3d test = new Vector3d(10, 10, 10);
//        StringBuilder builder = new StringBuilder().append(test.toString());
//        builder.deleteCharAt(0);
//        builder.deleteCharAt(builder.length()-1);
//        for (int i = 0; i < builder.length(); i++){
//            if (builder.charAt(i) == ' '){
//                builder.deleteCharAt(i);
//            }
//        }
//        String[] loc = builder.toString().split(",");
//        Vector3d test2 = new Vector3d(Float.parseFloat(loc[0]) , Float.parseFloat(loc[1]), Float.parseFloat(loc[2]));
//        System.out.println("test2 = " + test2);
//    }

    public static void main(String[] args) {
        new Test();
    }

    public Test(){
        long time1 = System.nanoTime();
        String[][] test = factoriseMessage("bonjour en fait");
        long time2 = System.nanoTime();
        System.out.println("time= "+(time2-time1));
        Arrays.stream(test).forEach(message -> {
            Arrays.stream(message).forEach(subMessage -> {
                System.out.println("subMessage = " + subMessage);
            });
        });
    }

    private String[][] factoriseMessage(String message){
        long time1 = System.currentTimeMillis();
        StringBuilder line1 = new StringBuilder(message);
        StringBuilder line2 = new StringBuilder(message);
        StringBuilder line3 = new StringBuilder(message);
        StringBuilder line4 = new StringBuilder(message);
        System.out.println("message.length() = " + message.length());
        switch ((int)Math.floor((float)message.length()/40)){
            case 0:
                //line1.delete(40, line1.length()-1);
                return new String[][]{{line1.toString()}, {""}, {""}, {""}};
            case 1:
                line1.delete(40, line1.length());
                line2.delete(0, 40);
//                line2.delete(80, line2.length()-1);
                return new String[][]{{line1.toString()}, {line2.toString()}, {""}, {""}};
            case 2:
                line1.delete(40, line1.length());
                line2.delete(80, line2.length());
                line2.delete(0, 40);
                line3.delete(0, 80);
//                line3.delete(120, line3.length()-1);
                return new String[][]{{line1.toString()}, {line2.toString()}, {line3.toString()}, {""}};
            case 3:
                line1.delete(40, line1.length());
                line2.delete(80, line2.length());
                line3.delete(120, line3.length());
                line2.delete(0, 40);
                line3.delete(0, 80);
//                line3.delete(120, line3.length());
                line4.delete(0, 120);
//                line4.delete(160, line4.length()-1);
                return new String[][]{{line1.toString()}, {line2.toString()}, {line3.toString()}, {line4.toString()}};
//            case 4:
//                line1.delete(40, 80);
//                line2.delete(80, 120);
//                line3.delete(120, 160);
//                line4.delete(160, line3.length()-1);
//                break;
        }
        long time2 = System.currentTimeMillis();
        System.out.println("time="+(time2-time1));
        return new String[][]{{"error"}, {"error"}, {"error"}, {"error"}};
    }
}