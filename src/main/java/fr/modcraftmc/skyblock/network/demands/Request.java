package fr.modcraftmc.skyblock.network.demands;

public enum Request {
    PLAYER_LIST, MEMBER_LIST, BOTH, MAIN, SETTINGS, PERMISSIONS, INFOS, CONFIGURATION, PLAYERISLANDS, ISLANDINFOS, ERROR;

    public static int getId(Request request){
        switch (request){
            case MEMBER_LIST:
                return 0;
            case PLAYER_LIST:
                return 1;
            case BOTH:
                return 2;
            case MAIN:
                return 3;
            case SETTINGS:
                return 4;
            case PERMISSIONS:
                return 5;
            case INFOS:
                return 6;
            default:
                return -1;
        }
    }

    public static Request toId(int id){
        switch (id){
            case 0:
                return MEMBER_LIST;
            case 1:
                return PLAYER_LIST;
            case 2:
                return BOTH;
            case 3:
                return MAIN;
            case 4:
                return SETTINGS;
            case 5:
                return PERMISSIONS;
            case 6:
                return INFOS;
            default:
                return null;
        }
    }
}
