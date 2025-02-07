package net.Mirik9724.whitelist_ultra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.Mirik9724.whitelist_ultra.commands.WhitelistUltraCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Whitelist_ultra extends JavaPlugin {
    private static Whitelist_ultra instance;  // Статическая переменная для хранения экземпляра плагина
    private File translationsFile; // Объявляем переменную для файла переводов
    private static FileConfiguration translationsConfig;
    public String VersionOfPlugin = this.getDescription().getVersion();
    private static final String NewestVersionOfPlugin = "https://raw.githubusercontent.com/Mirik9724/whitelist-ultra/main/V.txt";

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

        //int pluginId = 24668;
        //Metrics metrics = new Metrics(this, pluginId);
        //getLogger().info("Metrics are enabled.");


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

    //public static String getTranslation(String path, Object... args) {
    //    String message = translationsConfig.getString(path, "Translation not found: " + path);
    //    if (message != null && args.length > 0) {
    //        return String.format(message, args);
    //    }
    //    return message;
    //}

    public static String getTranslation(String path, Object... args) {
        String message = translationsConfig.getString(path, "Translation not found: " + path);

        if (message != null && args.length > 0) {
            message = String.format(message, args);
        }

        // Поддержка цветных сообщений с кодами типа &c (красный), &6 (золотой)
        message = ChatColor.translateAlternateColorCodes('&', message);

        // Поддержка градиентов с помощью MiniMessage
        // Например: "<gradient:red:yellow>Текст</gradient>"
        Component component = MiniMessage.miniMessage().deserialize(message);

        // Преобразуем обратно в строку, чтобы использовать в старых версиях
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    public void checkForUpdates(CommandSender sender) {
        try {
            // Получаем версию плагина с сайта
            URL url = new URL(NewestVersionOfPlugin);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String latestVersion = response.toString().trim();

            // Убираем буквы и сравниваем только числа
            String latestNumeric = latestVersion.replaceAll("[^0-9.]", "");
            String pluginNumeric = VersionOfPlugin.replaceAll("[^0-9.]", "");

            String latestLetter = latestVersion.replaceAll("[^a-zA-Z]", "");
            String pluginLetter = VersionOfPlugin.replaceAll("[^a-zA-Z]", "");

            // Разбиваем версии по точкам и сравниваем числа
            String[] latestParts = latestNumeric.split("\\.");
            String[] pluginParts = pluginNumeric.split("\\.");
            int maxLength = Math.max(latestParts.length, pluginParts.length);

            for (int i = 0; i < maxLength; i++) {
                int num1 = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
                int num2 = i < pluginParts.length ? Integer.parseInt(pluginParts[i]) : 0;

                if (num1 > num2) {
                    System.out.println(Whitelist_ultra.getTranslation("version.v_found") + "https://modrinth.com/plugin/whitelist-ultra/version/" + latestVersion);
                    return;
                } else if (num1 < num2) {
                    System.out.println(Whitelist_ultra.getTranslation("version.v_no_found"));
                    return;
                }
            }

            // Если числа совпадают, сравниваем буквы (b > a)
            if (latestLetter.compareTo(pluginLetter) > 0) {
                System.out.println(Whitelist_ultra.getTranslation("version.v_found") + "https://modrinth.com/plugin/whitelist-ultra/version/" + latestVersion);
            } else {
                System.out.println(Whitelist_ultra.getTranslation("version.v_no_found"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

}

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Whitelist Ultra has unloaded");
    }
}
