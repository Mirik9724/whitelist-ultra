package net.Mirik9724.whitelist_ultra;

import net.Mirik9724.whitelist_ultra.commands.Add;
import net.Mirik9724.whitelist_ultra.commands.WhitelistUltraCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Whitelist_ultra extends JavaPlugin {

    @Override
    public void onEnable() {
        String pluginDir = this.getDataFolder().getAbsolutePath();
        String fileName = "data.json";

        // Создайте объект File, представляющий файл
        File file = new File(pluginDir, fileName);

        try {
            // Создайте директории, если они не существуют
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Создайте файл, если он не существует
            if (file.createNewFile()) {
                getLogger().info("Файл успешно создан: " + file.getAbsolutePath());
            } else {
                getLogger().info("Файл уже существует.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        WhitelistUltraCommand commandExecutor = new WhitelistUltraCommand(this);
        getCommand("whitelist-ultra").setExecutor(commandExecutor);

        Bukkit.getPluginManager().registerEvents(new net.Mirik9724.whitelist_ultra.PlayerJoinListener(this), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Whitelist Ultra has load");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Whitelist Ultra has unload");
    }
}
