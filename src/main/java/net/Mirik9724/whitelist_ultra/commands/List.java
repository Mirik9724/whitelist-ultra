package net.Mirik9724.whitelist_ultra.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.Mirik9724.whitelist_ultra.Whitelist_ultra;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class List implements CommandExecutor {

    private final JavaPlugin plugin;

    public List(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Логика отображения списка игроков в вайтлисте
        StringBuilder whitelistPlayers = new StringBuilder(Whitelist_ultra.getTranslation("commands.list"));

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(plugin.getDataFolder(), "data.json"); // Убедитесь, что путь правильный

            if (file.exists()) {
                // Читаем JSON файл
                JsonNode rootNode = mapper.readTree(file);

                if (rootNode.isEmpty()) {
                    sender.sendMessage("JSON file is empty.");
                } else {
                    // Сбор имен игроков в строку
                    rootNode.forEach(node -> {
                        whitelistPlayers.append(node.asText()).append(" ");
                    });

                    // Отправляем пользователю информацию об игроках
                    sender.sendMessage(whitelistPlayers.toString().trim()); // Удаляем лишний пробел в конце
                }
            } else {
                sender.sendMessage("The file does not exist.");
            }

        } catch (IOException e) {
            sender.sendMessage("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
