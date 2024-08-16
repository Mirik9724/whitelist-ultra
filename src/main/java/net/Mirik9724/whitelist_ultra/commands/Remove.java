package net.Mirik9724.whitelist_ultra.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class Remove implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Использование: /whitelist-ultra remove <player>");
            return false;
        }

        String playerName = args[1];

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("playerdata.json");

            JsonNode rootNode;
            if (file.exists()) {
                // Читаем существующий JSON файл
                rootNode = mapper.readTree(file);

                // Приведение к ObjectNode и удаление значения
                ObjectNode objectNode = (ObjectNode) rootNode;
                objectNode.remove(playerName);

                // Записываем обновленный JSON в файл
                mapper.writeValue(file, rootNode);

                // Выводим сообщение
                sender.sendMessage("Игрок " + playerName + " удален из вайтлиста.");

            } else {
                System.out.println("File does not exist.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
