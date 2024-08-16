package net.Mirik9724.whitelist_ultra.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;


public class WhitelistUltraCommand implements CommandExecutor {

    private final JavaPlugin plugin; // Поле для хранения экземпляра плагина
    private final Map<String, CommandExecutor> subCommands = new HashMap<>();

    // Конструктор, который принимает экземпляр плагина
    public WhitelistUltraCommand(JavaPlugin plugin) {
        this.plugin = plugin; // Инициализация поля
        // Регистрируем подкоманды
        subCommands.put("add", new Add());
        subCommands.put("list", new List());
        subCommands.put("remove", new Remove());
        subCommands.put("reload", new Reload(plugin)); // Передача плагина в команду reload
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Пожалуйста, укажите подкоманду.");
            return false;
        }

        CommandExecutor subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand != null) {
            return subCommand.onCommand(sender, command, label, args);
        } else {
            sender.sendMessage("Неизвестная подкоманда: " + args[0]);
            return false;
        }
    }
}
