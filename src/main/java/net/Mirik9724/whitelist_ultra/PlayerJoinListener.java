package net.Mirik9724.whitelist_ultra;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PlayerJoinListener implements Listener {

    private final JavaPlugin plugin;

    public PlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        List<String> allowedPlayers = getAllowedPlayers();

        if (!allowedPlayers.contains(playerName)) {
            event.getPlayer().kickPlayer(ChatColor.RED + "You are not on the whitelist.");
        }
    }

    private List<String> getAllowedPlayers() {
        File file = new File(plugin.getDataFolder(), "data.json");
        if (!file.exists()) {
            return List.of(); // Возвращаем пустой список, если файл не существует
        }

        try {
            // Читаем файл и возвращаем список имен игроков
            String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
            return List.of(content.split("\\R")); // Разделяем строки по разделителю строк
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Возвращаем пустой список в случае ошибки
        }
    }
}
