package net.Mirik9724.whitelist_ultra.commands;

import net.Mirik9724.whitelist_ultra.Whitelist_ultra;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Reload implements CommandExecutor {

    private final JavaPlugin plugin;

    public Reload(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        sender.sendMessage(Whitelist_ultra.getTranslation("plugin.reload"));
        return true;
    }
}