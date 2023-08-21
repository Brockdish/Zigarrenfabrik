package dev.jusoft.zigarrenfabrik;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationCommand implements CommandExecutor, TabCompleter {

    List<String> allLocations;
    HashMap<String, Location> locationUndoList = new HashMap<>();
    HashMap<String, Location> removedLocations = new HashMap<>();

    String getStringFromConfig(FileConfiguration config, String path) {

        return "(" + config.getString(path + ".World") + ", " +
                Util.round(config.getDouble(path + ".X"), 1) + " " +
                Util.round(config.getDouble(path + ".Y"), 1) + " " +
                Util.round(config.getDouble(path + ".Z"), 1) + ")";

    }

    Location getLocationFromConfig(FileConfiguration config, String path) {

        return new Location(Bukkit.getWorld(config.getString(path + ".World")),
                Util.round(config.getDouble(path + ".X"), 1),
                Util.round(config.getDouble(path + ".Y"), 1),
                Util.round(config.getDouble(path + ".Z"), 1));

    }

    void saveLocationToConfig(Location location, String name, FileConfiguration config, boolean savePreviousLocationInUndoList) {
        if (savePreviousLocationInUndoList) locationUndoList.put(name, getLocationFromConfig(config, "Locations." + name));
        config.set("Locations." + name + ".World", location.getWorld().getName());
        config.set("Locations." + name + ".X", Util.round(location.getX(), 1));
        config.set("Locations." + name + ".Y", Util.round(location.getY(), 1));
        config.set("Locations." + name + ".Z", Util.round(location.getZ(), 1));
        Main.getPlugin().saveConfig();
    }

    Location getLocationFromValidArgs(Player player, String[] args) {

        World world = player.getWorld();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();

        if (args.length >= 5 && Util.areDoubles(args[2], args[3], args[4])) {
            x = Double.parseDouble(args[2]);
            y = Double.parseDouble(args[3]);
            z = Double.parseDouble(args[4]);

        } else if (args.length >= 6 && Util.areDoubles(args[3], args[4], args[5]) && Bukkit.getWorld(args[2]) != null) {
            world = Bukkit.getWorld(args[2]);
            x = Double.parseDouble(args[3]);
            y = Double.parseDouble(args[4]);
            z = Double.parseDouble(args[5]);
        }

        return new Location(world, x, y, z);

    }

    boolean locationDoesNotExist(FileConfiguration config, Player player, String locationName) {

        if (!config.isSet("Locations." + locationName)) {
            player.sendMessage("§cThe location §6" + locationName + "§c does not exist");
            return true;
        }
        return false;

    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;
        if (args.length <= 1 && !args[0].equals("list")) return false;

        FileConfiguration config = Main.getPlugin().getConfig();
        allLocations = config.getConfigurationSection("Locations").getKeys(false).stream().toList();

        switch (args[0]) {

            case "add" -> {
                if (config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c already exists");
                    return true;
                }
                removedLocations.remove(args[1]);
                saveLocationToConfig(getLocationFromValidArgs(player, args), args[1], config, false);
                player.sendMessage("§aAdded the location §2" + args[1] + " §a" + Util.locationToString(getLocationFromValidArgs(player, args)));
            }

            case "get" -> {
                if (locationDoesNotExist(config, player, args[1])) return true;
                player.sendMessage("§aThe location §2" + args[1] + "§a is at " + getStringFromConfig(config, "Locations." + args[1]));
                player.spawnParticle(Particle.FIREWORKS_SPARK, getLocationFromConfig(config, "Locations." + args[1]), 1000, 0, 50, 0, 0.01f);
            }

            case "overwrite" -> {
                if (locationDoesNotExist(config, player, args[1])) return true;
                saveLocationToConfig(player.getLocation(), args[1], config, true);
                player.sendMessage("§aOverwritten the location §2" + args[1] + " §a" + Util.locationToString(player.getLocation()));
            }

            case "remove" -> {
                if (locationDoesNotExist(config, player, args[1])) return true;
                removedLocations.put(args[1], getLocationFromConfig(config, "Locations." + args[1]));
                player.sendMessage("§aRemoved the location §2" + args[1] + " §a" + getStringFromConfig(config, "Locations." + args[1]));
                config.set("Locations." + args[1], null);
                Main.getPlugin().saveConfig();
            }

            case "rename" -> {
                if (locationDoesNotExist(config, player, args[1])) return true;
                else if (config.isSet("Locations." + args[2])) {
                    player.sendMessage("§cThe location §6" + args[2] + "§c already exists");
                    return true;
                }
                locationUndoList.put(args[2], locationUndoList.get(args[1]));
                locationUndoList.remove(args[1]);
                removedLocations.remove(args[2]);
                saveLocationToConfig(getLocationFromConfig(config, "Locations." + args[1]), args[2], config, false);
                config.set("Locations." + args[1], null);
                Main.getPlugin().saveConfig();
                player.sendMessage("§aRenamed location §2" + args[1] + "§a to §2" + args[2] + " §a" + getStringFromConfig(config, "Locations." + args[2]));
            }

            case "distance" -> {
                if (locationDoesNotExist(config, player, args[1])) return true;
                boolean distanceToSecondLocation = args.length >= 3 && config.isSet("Locations." + args[2]);
                if (getLocationFromConfig(config, "Locations." + args[1]).getWorld() != (distanceToSecondLocation ? getLocationFromConfig(config, "Locations." + args[2]) : player.getLocation()).getWorld()) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c (" + getLocationFromConfig(config, "Locations." + args[1]).getWorld().getName() + ") is not in the same world as " + (distanceToSecondLocation ? "the location §6" + args[2] + "§c (" + getLocationFromConfig(config, "Locations." + args[2]).getWorld().getName() + ")" : "you (" + player.getWorld().getName() + ")"));
                    return true;
                }
                player.sendMessage("§aThe location §2" + args[1] + " §a" + getStringFromConfig(config, "Locations." + args[1]) + " is §2" + Util.round(getLocationFromConfig(config, "Locations." + args[1])
                        .distance(distanceToSecondLocation ? getLocationFromConfig(config, "Locations." + args[2]) : player.getLocation()), 2) + "§a blocks away from " + (distanceToSecondLocation ? "the location §2" + args[2] + "§a " + getStringFromConfig(config, "Locations." + args[2]) : "you"));
            }

            case "setposition" -> {
                if (locationDoesNotExist(config, player, args[1])) return true;
                saveLocationToConfig(getLocationFromValidArgs(player, args), args[1], config, true);
                player.sendMessage("§aThe location §2" + args[1] + " §ais now at " + Util.locationToString(getLocationFromValidArgs(player, args)));
            }

            case "undo" -> {
                if (locationDoesNotExist(config, player, args[1])) return true;
                if (!locationUndoList.containsKey(args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + " §c" + Util.locationToString(getLocationFromValidArgs(player, args)) + " was not altered since the last server restart/reload");
                    return true;
                }
                saveLocationToConfig(locationUndoList.get(args[1]), args[1], config, true);
                player.sendMessage("§aUndone the position of the location §2" + args[1] + "§a from " + Util.locationToString(locationUndoList.get(args[1])) + " to " + getStringFromConfig(config, "Locations." + args[1]));
            }

            case "restore" -> {
                if (!removedLocations.containsKey(args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c " + (allLocations.contains(args[1]) ? Util.locationToString(getLocationFromConfig(config, "Locations." + args[1])) + " exists" : "was not removed since the last server restart/reload and does not exist"));
                    return true;
                }
                saveLocationToConfig(removedLocations.get(args[1]), args[1], config, false);
                player.sendMessage("§aRestored the location §2" + args[1] + " §a" + Util.locationToString(removedLocations.get(args[1])));
                removedLocations.remove(args[1]);
            }

            case "list" -> {
                if (allLocations.isEmpty()) {
                    player.sendMessage("§cThere are no locations");
                    return true;
                }
                player.sendMessage(allLocations.size() == 1 ? "§aThere is 1 location:" : "§aThere are " + allLocations.size() + " locations:");
                for (String s : allLocations) {
                    player.sendMessage("§2" + s + "§a: " + Util.locationToString(getLocationFromConfig(config, "Locations." + s)));
                }
            }

        }

        return true;

    }

    @Override public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> validArguments = List.of("add", "distance", "overwrite", "get", "list", "remove", "rename", "restore", "setposition", "undo");
        if (args.length >= 2 && !validArguments.contains(args[0])) return List.of("Invalid argument '" + args[0] + "'");

        switch (args.length) {

            case 1 -> {
                return validArguments;
            }

            case 2 -> {
                switch (args[0]) {
                    case "add" -> {
                        return List.of("Name");
                    }
                    case "get", "overwrite", "remove", "rename", "distance", "setposition", "undo" -> {
                        return allLocations;
                    }
                    case "restore" -> {
                        return removedLocations.keySet().stream().toList();
                    }
                }
            }

            case 3 -> {
                switch (args[0]) {
                    case "rename" -> {
                        return List.of("NewName");
                    }
                    case "distance" -> {
                        return allLocations;
                    }
                    case "add", "setposition" -> {
                        List<String> list = new ArrayList<>();
                        list.add("0 0 0");
                        for (World world : Bukkit.getWorlds()) {
                            list.add(world.getName() + " 0 0 0");
                        }
                        return list;
                    }
                }
            }

            case 4, 5, 6 -> {
                switch (args[0]) {
                    case "add", "setposition" -> {
                        switch (args.length) {
                            case 4 -> {
                                if (!Util.isDouble(args[2])) {
                                    if (Bukkit.getWorld(args[2]) == null) return List.of("World " + args[2] + " doesn't exist");
                                    else return List.of("0 0 0");
                                }
                                else return List.of("0 0");
                            }
                            case 5 -> {
                                if (!Util.isDouble(args[2])) {
                                    if (Bukkit.getWorld(args[2]) == null) return List.of("World " + args[2] + " doesn't exist");
                                    else return List.of("0 0");
                                }
                                else return List.of("0");
                            }
                            case 6 -> {
                                if (!Util.isDouble(args[2])) {
                                    if (Bukkit.getWorld(args[2]) == null) return List.of("World " + args[2] + " doesn't exist");
                                    else return List.of("0");
                                }
                                else return List.of();
                            }
                        }
                    }
                }
            }

        }

        return List.of();

    }
}
