package dev.jusoft.zigarrenfabrik;

import org.bukkit.Location;

public class Util {

    static boolean isDouble(String string) {

        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    static double round(double var, int decimals) {

        return Math.round((var) * Math.pow(10, decimals)) / Math.pow(10, decimals);

    }

    static String locationToString(Location location) {

        return "(" + location.getWorld().getName() + ", " +
                round(location.getX(), 1) + " " +
                round(location.getY(), 1) + " " +
                round(location.getZ(), 1) + ")";
    }

}
