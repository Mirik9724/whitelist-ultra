package net.Mirik9724.whitelist_ultra.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.Mirik9724.whitelist_ultra.Whitelist_ultra;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Remove implements CommandExecutor {

    private final JavaPlugin plugin;

    public Remove(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Whitelist_ultra.getTranslation("plugin.commands.usage") + " /whitelist-ultra remove <player>");
            return false;
        }

        String playerName = args[1];

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(plugin.getDataFolder(), "data.json");

            if (file.exists()) {
                JsonNode rootNode = mapper.readTree(file);
                if (rootNode.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) rootNode;

                    // Создаем новый массив, исключая игрока
                    ArrayNode newArrayNode = mapper.createArrayNode();
                    boolean found = false;

                    for (JsonNode node : arrayNode) {
                        if (!node.asText().equalsIgnoreCase(playerName)) {
                            newArrayNode.add(node);
                        } else {
                            found = true; // Игрок найден для удаления
                        }
                    }

                    if (found) {
                        // Записываем обновленный массив в файл
                        mapper.writeValue(file, newArrayNode);
                        sender.sendMessage(Whitelist_ultra.getTranslation("plugin.commands.player") + playerName + " удален из вайтлиста.");
                    } else {
                        sender.sendMessage(Whitelist_ultra.getTranslation("plugin.commands.player") + playerName + " не найден в вайтлисте.");
                    }
                } else {
                    sender.sendMessage("The file contains an incorrect format. Expecting an array.");
                }
            } else {
                sender.sendMessage("Whitelist file not found.");
            }

        } catch (IOException e) {
            sender.sendMessage("Error when removing a player from the whitelist: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
