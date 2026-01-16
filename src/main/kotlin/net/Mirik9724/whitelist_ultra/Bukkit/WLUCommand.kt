package net.Mirik9724.whitelist_ultra.Bukkit

import net.Mirik9724.whitelist_ultra.Commands.wld
import net.Mirik9724.whitelist_ultra.WLUCore
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class WLUCommand() : CommandExecutor, TabCompleter {

    private val subCommands: MutableMap<String, CommandExecutor> = HashMap()

    init {
        subCommands["add"] = AddBukkit()
        subCommands["list"] = ListBukkit()
        subCommands["change"] = ChangeBukkit()
        subCommands["remove"] = RemoveBukkit()
        subCommands["del"] = RemoveBukkit()
        subCommands["reload"] = ReloadBukkit()
        subCommands["check"] = CheckBukkit()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(WLUCore.gT("commands.subc").toString())
            return false
        }

        val subCommand = subCommands[args[0].lowercase()]
        return if (subCommand != null) {
            subCommand.onCommand(sender, command, label, args)
        } else {
            sender.sendMessage(WLUCore.gT("error.underknewcom").toString().replace("@undsubcom", args[0]))
            false
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        if (args.size == 1) {
            val input = args[0].lowercase()
            return subCommands.keys
                .filter { it.startsWith(input) }
                .toList()
        }

        if (args.size == 2) {
            val sub = args[0].lowercase()
            if (sub == "remove" || sub == "del" || sub == "check" || sub == "change") {
                val input = args[1].lowercase()
                val whitelistPlayers = wld.map { it.asText() }
                return whitelistPlayers
                    .filter { it.lowercase().startsWith(input) }
                    .toList()
            }
        }
        return emptyList()
    }
}
