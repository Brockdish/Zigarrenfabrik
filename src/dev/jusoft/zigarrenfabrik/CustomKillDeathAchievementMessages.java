package dev.jusoft.zigarrenfabrik;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.List;

public class CustomKillDeathAchievementMessages implements Listener {

    Sound deathSound = Sound.ENTITY_ENDER_DRAGON_GROWL;
    Sound killSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    FileConfiguration config = Main.getPlugin().getConfig();

    void checkIfValidValuesInConfig() {

        if (!config.isBoolean("CustomDeathMessages")) config.set("CustomDeathMessages", true);
        if (!config.isBoolean("PlayerKillMessages")) config.set("PlayerKillMessages", true);
        if (!config.isBoolean("EntityKillMessages")) config.set("EntityKillMessages", true);
        if (!config.isBoolean("CustomAchievementMessages")) config.set("CustomAchievementMessages", true);

    }

    @EventHandler void onPlayerDeath(PlayerDeathEvent event) {

        checkIfValidValuesInConfig();

        if (! (boolean) config.get("CustomDeathMessages")) return;

        event.setDeathMessage("§c" + event.getDeathMessage() + " (-" + event.getEntity().getTotalExperience() + " XP / " + event.getEntity().getLevel() + " lvl)");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, deathSound, 1, 0.5f);
        }
        event.getEntity().playSound(event.getEntity().getLocation(), deathSound, 1, 0.5f);

        config.set("DeathInventories." + event.getEntity().getName(), event.getEntity().getInventory());
        Main.getPlugin().saveConfig();

    }

    @EventHandler void onEntityDeath(EntityDeathEvent event) {

        checkIfValidValuesInConfig();

        if (! (boolean) config.get("PlayerKillMessages")) return;

        if (event.getEntity().getKiller() == null) return;
        Player player = event.getEntity().getKiller();

        Bukkit.broadcastMessage("§e" + player.getName() + " killed " + event.getEntity().getName() + " ( +" + event.getDroppedExp() + " XP )");
        player.getWorld().playSound(player.getLocation(), killSound, 1, 2);

    }

    @EventHandler void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        checkIfValidValuesInConfig();

        if (! (boolean) config.get("EntityKillMessages")) return;

        if (!(event.getEntity() instanceof LivingEntity damaged) || event.getDamager() instanceof Player) return;
        if (damaged.getHealth() - event.getFinalDamage() > 0) return;

        Bukkit.broadcastMessage("§e" + event.getDamager().getName() + " killed " + damaged.getName());

    }

    @EventHandler void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {

        checkIfValidValuesInConfig();

        if (! (boolean) config.get("CustomAchievementMessages")) return;

        Player player = event.getPlayer();

        int xpBeforeEvent = player.getTotalExperience();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
            if (player.getTotalExperience() - xpBeforeEvent <= 0) return;
            Bukkit.broadcastMessage("+" + (player.getTotalExperience() - xpBeforeEvent) + " XP");
        }, 1);



    }

}
