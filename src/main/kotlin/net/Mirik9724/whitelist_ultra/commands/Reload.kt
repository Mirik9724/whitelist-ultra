package net.Mirik9724.whitelist_ultra.commands

import net.Mirik9724.whitelist_ultra.WLU
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class Reload(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.reloadConfig()
        sender.sendMessage(WLU.instance.gT("reload"))
        return true
    }
}
