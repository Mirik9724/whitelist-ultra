package net.Mirik9724.whitelist_ultra.Sp

import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL
import net.Mirik9724.whitelist_ultra.WLUCore.whitelist_f
import net.Mirik9724.whitelist_ultra.commands.ChangeCommand
import net.Mirik9724.whitelist_ultra.commands.*
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin


class AddBukkit(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLUCore.gT("commands.usage") + " /whitelist-ultra add <player>")
            return false
        }

        val nick = args[1]
        val result = AddCommand.execute(nick)

        sender.sendMessage(result)
        return true
    }
}

class ChangeBukkit(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 3) {
            sender.sendMessage(WLUCore.gT("commands.usage") + " /whitelist-ultra change <old> <new>")
            return false
        }

        val oldNick = args[1]
        val newNick = args[2]
        val result = ChangeCommand.execute(oldNick, newNick)

        sender.sendMessage(result)
        return true
    }
}


class CheckBukkit(private val plugin: JavaPlugin) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLUCore.gT("commands.usage") + " /whitelist-ultra check <player>")
            return false
        }

        val nick = args[1]
        val result = CheckCommand.execute(nick)

        sender.sendMessage(result)
        return true
    }
}

class ListBukkit(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val result = ListCommand.execute(dirWL)
        sender.sendMessage(result)
        return true
    }
}

class RemoveBukkit(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLUCore.gT("commands.usage") + " /whitelist-ultra remove <player>")
            return false
        }

        val nick = args[1]

        val result = RemoveCommand.removeFromWhitelist(nick)

        if (result) {
            sender.sendMessage(WLUCore.gT("commands.remove.m1"))
            Bukkit.getPlayerExact(nick)?.let {
                if (it.isOnline) {
                    it.kickPlayer(WLUCore.gT("commands.remove.kick"))
                }
            }
        } else {
            sender.sendMessage(WLUCore.gT("commands.remove.m1")) // same message for "not found"
        }

        return true
    }
}

class ReloadBukkit(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.reloadConfig()
        sender.sendMessage(ReloadCommand.reload())
        return true
    }
}
