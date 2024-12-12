package net.Mirik9724.whitelist_ultra.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.Mirik9724.whitelist_ultra.Whitelist_ultra;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Add implements CommandExecutor {
    private final JavaPlugin plugin;

    // Конструктор с передачей экземпляра плагина
    public Add(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Whitelist_ultra.getTranslation("commands.usage") +" /whitelist-ultra add <player>");
            return false;
        }

        String playerName = args[1];

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(plugin.getDataFolder(), "data.json");

            // Если файл не существует, создаем новый
            ArrayNode arrayNode;
            if (file.exists()) {
                // Читаем существующий JSON файл
                JsonNode rootNode = mapper.readTree(file);
                // Проверяем, является ли корневой узел массивом
                if (rootNode.isArray()) {
                    arrayNode = (ArrayNode) rootNode; // Приводим к ArrayNode
                } else {
                    sender.sendMessage("The file contains an incorrect format. A new whitelist is created.");
                    arrayNode = mapper.createArrayNode(); // Создаем новый массив
                }
            } else {
                // Создаем новый JSON массив, если файл не существует
                arrayNode = mapper.createArrayNode();
            }

            // Проверяем, есть ли игрок уже в списке
            boolean exists = false;
            for (JsonNode node : arrayNode) {
                if (node.asText().equals(playerName)) {
                    exists = true;
                    break;
                }
            }

            // Если игрок не найден, добавляем его
            if (!exists) {
                arrayNode.add(playerName);
                mapper.writeValue(file, arrayNode); // Записываем обновленный массив в файл
                sender.sendMessage(Whitelist_ultra.getTranslation("commands.player") + playerName + Whitelist_ultra.getTranslation("plugin.commands.add.m1"));
            } else {
                sender.sendMessage(Whitelist_ultra.getTranslation("commands.player") + playerName + Whitelist_ultra.getTranslation("plugin.commands.add.m2"));
            }

        } catch (IOException e) {
            sender.sendMessage("Error when adding a player to the whitelist: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }


}
