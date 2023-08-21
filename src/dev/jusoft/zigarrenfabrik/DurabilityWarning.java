package dev.jusoft.zigarrenfabrik;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.meta.Damageable;

public class DurabilityWarning implements Listener {

    String colorCode;

    @EventHandler void onPlayerItemDamage(PlayerItemDamageEvent event) {

        Damageable damageable = (Damageable) event.getItem().getItemMeta();
        int durability = event.getItem().getType().getMaxDurability() - damageable.getDamage() - event.getDamage();

        switch (durability) {
            case 1500, 1000, 500 -> colorCode = "§a";
            case 400, 300, 200 -> colorCode = "§e";
            case 100, 50, 25 -> colorCode = "§6";
            case 10, 5, 4, 3, 2, 1 -> colorCode = "§c";
            default -> { return; }
        }

        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorCode + durability + " durability left (" + damageable.getDisplayName() + ")"));

    }

}
