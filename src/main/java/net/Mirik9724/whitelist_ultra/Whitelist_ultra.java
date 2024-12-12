package net.Mirik9724.whitelist_ultra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.Mirik9724.whitelist_ultra.commands.WhitelistUltraCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Whitelist_ultra extends JavaPlugin {
    private static Whitelist_ultra instance;  // Статическая переменная для хранения экземпляра плагина
    private File translationsFile; // Объявляем переменную для файла переводов
    private static FileConfiguration translationsConfig;
    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/whitelist-ultra/version";

    @Override
    public void onEnable() {
        String pluginDir = this.getDataFolder().getAbsolutePath();
        String fileName = "data.json";


        // Создайте объект File, представляющий файл
        File file = new File(pluginDir, fileName);

        // Создайте директории, если они не существуют
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // Создайте файл, если он не существует
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    getLogger().info("File created successfully: " + file.getAbsolutePath());
                } else {
                    getLogger().info("The file already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WhitelistUltraCommand commandExecutor = new WhitelistUltraCommand(this);
        getCommand("whitelist-ultra").setExecutor(commandExecutor);

        loadTranslations();
        checkForUpdates(Bukkit.getConsoleSender());

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Whitelist Ultra has loaded");
    }

    public static Whitelist_ultra getInstance() {
        return instance; // Предоставляем доступ к экземпляру
    }

    public void loadTranslations() {
        translationsFile = new File(getDataFolder(), "messages.yml");

        // Если файл отсутствует, копируем из ресурсов
        if (!translationsFile.exists()) {
            getLogger().info("File messages.yml is not found. Trying to create from resources...");
            saveResource("messages.yml", false);
            getLogger().info("File messages.yml is successfully created.");
        }

        // Загружаем содержимое файла
        translationsConfig = YamlConfiguration.loadConfiguration(translationsFile);

        // Загружаем эталонный файл из ресурсов
        InputStream defaultStream = getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));

            // Добавляем только отсутствующие ключи
            boolean updated = false;
            for (String key : defaultConfig.getKeys(true)) {
                if (!translationsConfig.contains(key)) { // Если ключ отсутствует
                    translationsConfig.set(key, defaultConfig.getString(key));
                    updated = true;
                }
            }

            // Если файл был обновлен, сохраняем изменения
            if (updated) {
                try {
                    translationsConfig.save(translationsFile);
                    getLogger().info("Translations file was updated with new entries.");
                } catch (IOException e) {
                    getLogger().severe("Failed to save updated translations file: " + e.getMessage());
                }
            } else {
                getLogger().info("Translations file is already up-to-date.");
            }
        } else {
            getLogger().severe("Default translations file (messages.yml) is missing in plugin resources.");
        }
    }

    public static String getTranslation(String path, Object... args) {
        String message = translationsConfig.getString(path, "Translation not found: " + path);
        if (message != null && args.length > 0) {
            return String.format(message, args);
        }
        return message;
    }

    public void checkForUpdates(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(MODRINTH_API_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() != 200) {
                    getLogger().severe("Failed to check for updates: HTTP " + connection.getResponseCode());
                    return;
                }

                InputStream inputStream = connection.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode versions = mapper.readTree(inputStream);

                if (versions.isArray() && versions.size() > 0) {
                    JsonNode latestVersion = versions.get(0);
                    String latestVersionNumber = latestVersion.get("version_number").asText();
                    String downloadUrl = latestVersion.get("files").get(0).get("url").asText();

                    String currentVersion = getDescription().getVersion();
                    if (!currentVersion.equalsIgnoreCase(latestVersionNumber)) {
                        sender.sendMessage(ChatColor.GREEN + "New version of Whitelist Ultra is available: " + latestVersionNumber);

                        if (sender instanceof Player player) {
                            TextComponent message = new TextComponent(ChatColor.YELLOW + "Download it here: " + downloadUrl);
                            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl));
                            player.spigot().sendMessage(message); // Для игрока
                        } else {
                            sender.sendMessage(ChatColor.YELLOW + "Download it here: " + downloadUrl); // Для консоли
                        }

                        getLogger().info("New version available: " + latestVersionNumber + ". Download it at " + downloadUrl);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Whitelist Ultra is up to date.");
                        getLogger().info("Whitelist Ultra is up to date.");
                    }
                } else {
                    getLogger().info("No versions found on Modrinth.");
                }

                inputStream.close();
            } catch (Exception e) {
                getLogger().severe("Failed to check for updates: " + e.getMessage());
            }
        });

}

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Whitelist Ultra has unloaded");
    }
}
