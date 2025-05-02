package net.Mirik9724.whitelist_ultra.commands

import net.Mirik9724.whitelist_ultra.WLU
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class WLUCommand(private val plugin: JavaPlugin) : CommandExecutor {

    private val subCommands: MutableMap<String, CommandExecutor> = HashMap()

    init {
        // Registering subcommands
        subCommands["add"] = Add(plugin)
        subCommands["list"] = List(plugin)
        subCommands["remove"] = Remove(plugin)
        subCommands["reload"] = Reload(plugin)
        subCommands["check"] = Check(plugin)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(WLU.instance.gT("commands.error.subc"))
            return false
        }

        val subCommand = subCommands[args[0].lowercase()]
        return if (subCommand != null) {
            subCommand.onCommand(sender, command, label, args)
        } else {
            sender.sendMessage(WLU.instance.gT("commands.error.underknewcom").replace("@undsubcom", args[0]))
            false
        }
    }
}
