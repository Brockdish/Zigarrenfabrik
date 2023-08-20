package dev.jusoft.zigarrenfabrik;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class BeefBoss implements Listener {

    void createCraftingRecipe() {

        ItemStack beefBoss = beefBoss(1);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(Main.getPlugin(), "beefboss"), beefBoss);
        recipe.shape("B", "S", "B");
        recipe.setIngredient('B', Material.BREAD);
        recipe.setIngredient('S', Material.COOKED_BEEF);
        Bukkit.addRecipe(recipe);

    }

    ItemStack beefBoss(int amount) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setDisplayName("§eBeef Boss");
        GameProfile profile = new GameProfile(UUID.fromString("5bed3331-6b08-4111-a3b3-9f50a118b271"), "");
        profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlNWQ2NTZjNjM1Mjc4MjJjMjE3ZjQyNjdkYTBiNzUyNmU2NTQyNTRiNDFlNDA3N2VhNjc3YmM3Nzg2M2M1YiJ9fX0="));
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            Bukkit.getLogger().warning("Wrong Textures");
        }
        itemStack.setItemMeta(skullMeta);
        itemStack.setAmount(amount);
        return itemStack;
    }

    boolean isBeefBoss(ItemStack itemStack) {

        if (itemStack.getType() != Material.PLAYER_HEAD) return false;
        if (itemStack.toString().split("name=textures, value=").length == 1) return false;
        return (itemStack.toString().split("name=textures, value=")[1].split(", signature=")[0].equals
                ("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFlNWQ2NTZjNjM1Mjc4MjJjMjE3ZjQyNjdkYTBiNzUyNmU2NTQyNTRiNDFlNDA3N2VhNjc3YmM3Nzg2M2M1YiJ9fX0="));

    }

    @EventHandler void onPlayerEatBeefBoss(PlayerInteractEvent event) {

        ItemStack itemStack = event.getItem();

        if (itemStack == null || !isBeefBoss(itemStack)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

        Player player = event.getPlayer();

        if (player.getFoodLevel() >= 20) return;

        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

        player.setFoodLevel((player.getFoodLevel() + 2*5 + 8));
        player.setSaturation(player.getSaturation() + 2*6 + 12.8f);

        if (player.getFoodLevel() > 20) player.setFoodLevel(20);
        if (player.getSaturation() > player.getFoodLevel()) player.setSaturation(player.getFoodLevel());

        itemStack.setAmount(itemStack.getAmount() - 1);

    }

    @EventHandler void onPlayerPickupBeefBoss(EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        int amount = event.getItem().getItemStack().getAmount();

        if (isBeefBoss(event.getItem().getItemStack())) event.getItem().setItemStack(beefBoss(amount));

    }

    @EventHandler void onPlayerDropBeefBoss(EntityDropItemEvent event) {

        if (!(event.getEntity() instanceof Player)) return;
        int amount = event.getItemDrop().getItemStack().getAmount();

        if (isBeefBoss(event.getItemDrop().getItemStack())) event.getItemDrop().setItemStack(beefBoss(amount));

    }

}
