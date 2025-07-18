package net.Mirik9724.whitelist_ultra.Sp

import net.Mirik9724.whitelist_ultra.WLUCore
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class WLUCommand(private val plugin: JavaPlugin) : CommandExecutor {

    private val subCommands: MutableMap<String, CommandExecutor> = HashMap()

    init {
        // Registering subcommands
        subCommands["add"] = AddBukkit(plugin)
        subCommands["list"] = ListBukkit(plugin)
        subCommands["remove"] = RemoveBukkit(plugin)
        subCommands["reload"] = ReloadBukkit(plugin)
        subCommands["check"] = CheckBukkit(plugin)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(WLUCore.gT("commands.subc"))
            return false
        }

        val subCommand = subCommands[args[0].lowercase()]
        return if (subCommand != null) {
            subCommand.onCommand(sender, command, label, args)
        } else {
            sender.sendMessage(WLUCore.gT("commands.error.underknewcom").replace("@undsubcom", args[0]))
            false
        }
    }
}
