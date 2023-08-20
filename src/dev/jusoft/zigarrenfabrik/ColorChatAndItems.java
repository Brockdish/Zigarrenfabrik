package dev.jusoft.zigarrenfabrik;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareInventoryResultEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ColorChatAndItems implements Listener {

    @EventHandler void onPlayerChat(AsyncPlayerChatEvent event) {

        event.setMessage(event.getMessage().replaceAll("&", "§"));

    }

    @EventHandler void onPlayerRenameItem(PrepareInventoryResultEvent event) {

        if (!(event.getInventory() instanceof AnvilInventory)) return;

        AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
        ItemMeta itemMeta = anvilInventory.getItem(2).getItemMeta();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replaceAll("&", "§"));
        anvilInventory.getItem(2).setItemMeta(itemMeta);

    }

}
