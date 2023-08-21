package dev.jusoft.zigarrenfabrik;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main pluginVar;

    public void onEnable() {

        pluginVar = this;

        getCommand("location").setExecutor(new LocationCommand());
        getCommand("location").setTabCompleter(new LocationCommand());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BeefBoss(), this);
        pm.registerEvents(new DurabilityWarning(), this);
        pm.registerEvents(new AdditionalSounds(), this);
        pm.registerEvents(new HealthInActionbar(), this);
        pm.registerEvents(new ColorChatAndItems(), this);
        pm.registerEvents(new CustomKillDeathAchievementMessages(), this);

        new BeefBoss().createCraftingRecipe();

    }

    public static Main getPlugin() {
        return pluginVar;
    }

}
