package net.Mirik9724.whitelist_ultra.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;

public class List implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Логика отображения списка игроков в вайтлисте
        sender.sendMessage("Список игроков в вайтлисте: ...");

        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("playerdata.json");

            if (file.exists()) {
                // Читаем JSON файл
                JsonNode rootNode = mapper.readTree(file);

                if (rootNode.isEmpty()) {
                    System.out.println("JSON file is empty.");
                } else {
                    // Преобразуем JSON в строку
                    String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                    System.out.println("JSON Content:\n" + jsonString);
                }
            } else {
                System.out.println("File does not exist.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
