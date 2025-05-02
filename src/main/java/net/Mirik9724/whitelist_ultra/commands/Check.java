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

public class Check implements CommandExecutor {

    private final JavaPlugin plugin;

    public Check(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Whitelist_ultra.getTranslation("commands.usage") + " /whitelist-ultra check <player>");
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

                    for (JsonNode node : arrayNode) {
                        if (node.asText().equalsIgnoreCase(playerName)) {
                            sender.sendMessage(Whitelist_ultra.getTranslation("commands.player") + playerName + Whitelist_ultra.getTranslation("commands.check.found"));
                            return true;
                        }
                    }
                    sender.sendMessage(Whitelist_ultra.getTranslation("commands.player") + playerName + Whitelist_ultra.getTranslation("commands.check.not_found"));
                } else {
                    sender.sendMessage("The file contains an incorrect format. Expecting an array.");
                }
            } else {
                sender.sendMessage("Whitelist file not found.");
            }

        } catch (IOException e) {
            sender.sendMessage("Error when checking whitelist: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
