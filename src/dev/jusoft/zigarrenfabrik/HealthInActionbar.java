package dev.jusoft.zigarrenfabrik;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class HealthInActionbar implements Listener {

    String c = "§c";

    String healthbar(double percentage) {

        return new StringBuffer("§c§l══════════").insert((int) Math.max(0, Math.ceil(percentage * 10)) + 4, "§f§l").toString();

    }

    void showHealth(Player player, LivingEntity entity, double damage) {

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                healthbar((entity.getHealth() - damage) / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) + "   " +
                        c + ((float) Util.round(entity.getHealth() - damage, 1) + "§r / " + c + entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));

    }

    @EventHandler void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {

        if (!(event.getRightClicked() instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) event.getRightClicked();
        showHealth(event.getPlayer(), livingEntity, 0);

    }

}
