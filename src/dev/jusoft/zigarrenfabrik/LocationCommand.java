package dev.jusoft.zigarrenfabrik;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LocationCommand implements CommandExecutor, TabCompleter {

    String getStringFromConfig(FileConfiguration config, String path) {

        return "(" + config.getString(path + ".World") + ", " +
                config.getDouble(path + ".X") + " " +
                config.getDouble(path + ".Y") + " " +
                config.getDouble(path + ".Z") + ")";

    }

    Location getLocationFromConfig(FileConfiguration config, String path) {

        return new Location(Bukkit.getWorld(config.getString(path + ".World")),
                config.getDouble(path + ".X"),
                config.getDouble(path + ".Y"),
                config.getDouble(path + ".Z"));

    }

    void saveLocationToConfig(Location location, String name, FileConfiguration config) {
        config.set("Locations." + name + ".World", location.getWorld().getName());
        config.set("Locations." + name + ".X", Util.round(location.getX(), 1));
        config.set("Locations." + name + ".Y", Util.round(location.getY(), 1));
        config.set("Locations." + name + ".Z", Util.round(location.getZ(), 1));
        Main.getPlugin().saveConfig();
    }

    List<String> allLocations() {

        return Main.getPlugin().getConfig().getConfigurationSection("Locations").getKeys(false).stream().toList();

    }

    Location getLocationFromValidArgs(Player player, String[] args) {

        World world = player.getWorld();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();

        if (Util.isDouble(args[2]) && Util.isDouble(args[3]) && Util.isDouble(args[4])) {
            x = Double.parseDouble(args[2]);
            y = Double.parseDouble(args[3]);
            z = Double.parseDouble(args[4]);

        } else if (Util.isDouble(args[5]) && Bukkit.getWorld(args[2]) != null) {
            world = Bukkit.getWorld(args[2]);
            x = Double.parseDouble(args[3]);
            y = Double.parseDouble(args[4]);
            z = Double.parseDouble(args[5]);
        }

        return new Location(world, x, y, z);

    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) return true;
        if (args.length <= 1) return false;

        FileConfiguration config = Main.getPlugin().getConfig();

        switch (args[0]) {

            case "add" -> {
                if (config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c already exists");
                    return true;
                }
                saveLocationToConfig(getLocationFromValidArgs(player, args), args[1], config);
                player.sendMessage("§aAdded the location §2" + args[1] + " §a" + Util.locationToString(getLocationFromValidArgs(player, args)));
            }

            case "get" -> {
                if (!config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c doesn't exist");
                    return true;
                }
                player.sendMessage("§aThe location §2" + args[1] + "§a is at " + getStringFromConfig(config, "Locations." + args[1]));
            }

            case "overwrite" -> {
                if (!config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c doesn't exist");
                    return true;
                }
                saveLocationToConfig(player.getLocation(), args[1], config);
                player.sendMessage("§aOverwritten the location §2" + args[1] + " §a" + Util.locationToString(player.getLocation()));
            }

            case "remove" -> {
                if (!config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c doesn't exist");
                    return true;
                }
                player.sendMessage("§aRemoved the location §2" + args[1] + " §a" + getStringFromConfig(config, "Locations." + args[1]));
                config.set("Locations." + args[1], null);
                Main.getPlugin().saveConfig();
            }

            case "rename" -> {
                if (!config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c doesn't exist");
                    return true;
                } else if (config.isSet("Locations." + args[2])) {
                    player.sendMessage("§cThe location §6" + args[2] + "§c already exists");
                    return true;
                }
                config.set("Locations." + args[1], null);
                saveLocationToConfig(player.getLocation(), args[2], config);
                player.sendMessage("§aRenamed location §2" + args[1] + "§a to §2" + args[2] + " §a" + getStringFromConfig(config, "Locations." + args[2]));
            }

            case "distance" -> {
                if (!config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c doesn't exist");
                    return true;
                }
                boolean distanceToOtherLocation = args.length >= 3 && config.isSet("Locations." + args[2]);
                player.sendMessage("§aThe location §2" + args[1] + " §a" + getStringFromConfig(config, "Locations." + args[1]) + " is §2" + Util.round(getLocationFromConfig(config, "Locations." + args[1])
                        .distance(distanceToOtherLocation ? getLocationFromConfig(config, "Locations." + args[2]) : player.getLocation()), 2) + "§a blocks away from " + (distanceToOtherLocation ? "the location §2" + args[2] + "§a " + getStringFromConfig(config, "Locations." + args[2]) : "you"));
            }

            case "setposition" -> {
                if (!config.isSet("Locations." + args[1])) {
                    player.sendMessage("§cThe location §6" + args[1] + "§c doesn't exist");
                    return true;
                }
                saveLocationToConfig(getLocationFromValidArgs(player, args), args[1], config);
                player.sendMessage("§aThe location §2" + args[1] + " §ais now at " + Util.locationToString(getLocationFromValidArgs(player, args)));
            }

        }

        return true;

    }

    @Override public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage("args.length " + args.length);
        if (args.length >= 3) {
            sender.sendMessage("args[2] " + args[2]);
            sender.sendMessage("args[2] isDouble " + Util.isDouble(args[2]));
        }

        switch (args.length) {

            case 1 -> {
                return List.of("add", "distance", "overwrite", "get", "remove", "rename", "setposition");
            }

            case 2 -> {
                switch (args[0]) {
                    case "add" -> {
                        return List.of("Name");
                    }
                    case "get", "overwrite", "remove", "rename", "distance", "setposition" -> {
                        return allLocations();
                    }
                }
            }

            case 3 -> {
                switch (args[0]) {
                    case "rename" -> {
                        return List.of("NewName");
                    }
                    case "distance" -> {
                        return allLocations();
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
                                return List.of(Util.isDouble(args[2]) ? "0" : "0 0");
                            }
                            case 6 -> {
                                if (!Util.isDouble(args[2])) return List.of("0");
                            }
                        }
                    }
                }
            }

        }

        return new ArrayList<>();

    }
}
