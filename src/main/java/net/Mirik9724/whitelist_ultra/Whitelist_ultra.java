package net.Mirik9724.whitelist_ultra;

import net.Mirik9724.whitelist_ultra.commands.WhitelistUltraCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class Whitelist_ultra extends JavaPlugin {
    private File translationsFile; // Объявляем переменную для файла переводов
    private static FileConfiguration translationsConfig;

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

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Whitelist Ultra has loaded");
    }

    public void loadTranslations() {
        // Определяем путь к файлу переводов
        translationsFile = new File(getDataFolder(), "messages.yml");

        // Если файл не существует, копируем его из ресурсов плагина
        if (!translationsFile.exists()) {
            saveResource("messages.yml", false);
        }

        // Загружаем содержимое файла в объект YamlConfiguration
        translationsConfig = YamlConfiguration.loadConfiguration(translationsFile);
    }
    public static String getTranslation(String path, Object... args) {
        String message = translationsConfig.getString(path, "Translation not found: " + path);
        if (message != null && args.length > 0) {
            return String.format(message, args);
        }
        return message;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Whitelist Ultra has unloaded");
    }
}
