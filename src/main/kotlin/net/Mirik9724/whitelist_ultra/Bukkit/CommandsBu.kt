package net.Mirik9724.whitelist_ultra.Bukkit

import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.Commands.Add
import net.Mirik9724.whitelist_ultra.Commands.Del
import net.Mirik9724.whitelist_ultra.Commands.Check
import net.Mirik9724.whitelist_ultra.Commands.Change
import net.Mirik9724.whitelist_ultra.Commands.List
import net.Mirik9724.whitelist_ultra.Commands.Reload
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class AddBukkit() : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLUCore.gT("commands.usage")+ " /wlu add <player>")
            return false
        }
        WLUCore.nick = args[1]
        sender.sendMessage(Add(args[1]))
        return true
    }
}

class ChangeBukkit() : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 3) {
            sender.sendMessage(WLUCore.gT("commands.usage") + " /wlu change <old> <new>")
            return false
        }
        sender.sendMessage(Change(args[1], args[2]))
        return true
    }
}


class CheckBukkit() : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLUCore.gT("commands.usage") + " /wlu check <player>")
            return false
        }
        WLUCore.nick = args[1]
        sender.sendMessage(Check(args[1]))
        return true
    }
}

class ListBukkit() : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        sender.sendMessage(List())
        return true
    }
}

class RemoveBukkit() : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage("${WLUCore.gT("commands.usage")} /wlu remove <player>")
            return false
        }
        WLUCore.nick = args[1]
        sender.sendMessage(Del(args[1]))

        Bukkit.getPlayerExact(args[1])?.let { player ->
            if (player.isOnline) {
                player.kickPlayer(WLUCore.gT("commands.remove.kick"))
            }
        }
        return true
    }
}

class ReloadBukkit() : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        sender.sendMessage(Reload())
        return true
    }
}
