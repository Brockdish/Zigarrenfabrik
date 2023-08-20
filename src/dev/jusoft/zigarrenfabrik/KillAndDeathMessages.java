package dev.jusoft.zigarrenfabrik;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillAndDeathMessages implements Listener {

    Sound deathSound = Sound.ENTITY_ENDER_DRAGON_GROWL;
    Sound killSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

    @EventHandler void onPlayerDeath(PlayerDeathEvent event) {

        event.setDeathMessage("§c" + event.getDeathMessage() + " " + Util.locationToString(event.getEntity().getLocation()));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, deathSound, 1, 0.5f);
        }
        event.getEntity().playSound(event.getEntity().getLocation(), deathSound, 1, 0.5f);

    }

    @EventHandler void onEntityDeath(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null) return;
        Player player = event.getEntity().getKiller();

        Bukkit.broadcastMessage("§e" + player.getName() + " killed " + event.getEntity().getName() + " (+" + event.getDroppedExp() + " XP)");
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), killSound, 1, 2);
        player.getWorld().playSound(player.getLocation(), killSound, 1, 2);

    }

}
