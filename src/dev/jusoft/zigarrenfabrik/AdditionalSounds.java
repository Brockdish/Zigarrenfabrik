package dev.jusoft.zigarrenfabrik;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AdditionalSounds implements Listener {

    int noteIndex;
    int pitch;
    int scheduler;
    float startPitchOnLeave;
    FileConfiguration config = Main.getPlugin().getConfig();

    void checkIfValidValuesInConfig() {

        if (!config.isInt("JoinLeaveSound.NoteIntervalInTicks")) config.set("JoinLeaveSound.NoteIntervalInTicks", 2);
        if (!config.isInt("JoinLeaveSound.PitchInterval")) config.set("JoinLeaveSound.PitchInterval", 3);
        if (!config.isInt("JoinLeaveSound.NoteAmount")) config.set("JoinLeaveSound.NoteAmount", 4);
        startPitchOnLeave = 0.5f + (config.getInt("JoinLeaveSound.NoteAmount") - 1) * config.getInt("JoinLeaveSound.PitchInterval") / 12f;

        if (!config.isBoolean("JoinSoundEnabled")) config.set("JoinSoundEnabled", true);
        if (!config.isBoolean("LeaveSoundEnabled")) config.set("LeaveSoundEnabled", true);
        if (!config.isBoolean("BowHitSoundEnabled")) config.set("BowHitSoundEnabled", true);

        Main.getPlugin().saveConfig();

    }

    void playJoinLeaveSound(boolean joined) {

        checkIfValidValuesInConfig();

        if (! (boolean) config.get(joined ? "JoinSoundEnabled" : "LeaveSoundEnabled")) return;

        noteIndex = 0;
        pitch = 0;
        Bukkit.getScheduler().cancelTask(scheduler);
        scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME , 10, (joined ? 0.5f : startPitchOnLeave) + pitch / 12f);
            }
            noteIndex++;
            pitch += (joined ? 1 : -1) * config.getInt("JoinLeaveSound.PitchInterval");
            if (noteIndex >= config.getInt("JoinLeaveSound.NoteAmount")) Bukkit.getScheduler().cancelTask(scheduler);

        }, 20, config.getInt("JoinLeaveSound.NoteIntervalInTicks"));

    }

    @EventHandler void onPlayerJoin(PlayerJoinEvent event) {

        playJoinLeaveSound(true);

    }

    @EventHandler void onPlayerQuit(PlayerQuitEvent event) {

        playJoinLeaveSound(false);

    }

    @EventHandler void onPlayerArrowHitEntity(EntityDamageByEntityEvent event) {

        checkIfValidValuesInConfig();

        if (!(event.getEntity() instanceof LivingEntity) || event.getFinalDamage() == 0 || ! (boolean) config.get("BowHitSoundEnabled")) return;

        switch (event.getDamager().getType()) {
            case ARROW, SPECTRAL_ARROW, FIREWORK -> {
                event.getDamager().getWorld().playSound(event.getDamager().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f);
                event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.5f);
            }
        }

    }

}
