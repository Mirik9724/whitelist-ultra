package net.Mirik9724.whitelist_ultra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PlayerJoinListener implements Listener {
    private final JavaPlugin plugin;

    public PlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        List<String> allowedPlayers = getAllowedPlayers();

        plugin.getLogger().info("Checking whitelist for player: " + playerName);

        if (!allowedPlayers.stream().map(String::trim).toList().contains(playerName.trim())) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.RED + Whitelist_ultra.getTranslation("kick"));
        }


    }

    private List<String> getAllowedPlayers() {
        File file = new File(plugin.getDataFolder(), "data.json");
        if (!file.exists()) {
            plugin.getLogger().warning("Whitelist file not found, returning an empty list.");
            return List.of(); // Возвращаем пустой список, если файл не существует
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(file);
            List<String> allowedPlayers = new ArrayList<>();

            // Проверяем, что корневой узел является массивом
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    allowedPlayers.add(node.asText());
                }
            } else {
                plugin.getLogger().warning("Invalid format in whitelist file. Expected an array.");
            }

            return allowedPlayers;
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Возвращаем пустой список в случае ошибки
        }
    }
}
