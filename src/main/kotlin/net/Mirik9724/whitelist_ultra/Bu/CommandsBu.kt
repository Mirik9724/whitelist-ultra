package net.Mirik9724.whitelist_ultra.Bu

import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL
import net.Mirik9724.whitelist_ultra.commands.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command
import java.io.File

class AddBungee : Command("whitelistadd") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.size < 1) {
            sender.sendMessage("Usage: /whitelist-ultra add <nick>")
            return
        }

        val nick = args[0]
        val result = AddCommand.execute(nick)

        sender.sendMessage(result)
    }
}

class CheckBungee : Command("whitelistcheck") {

    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage("Usage: /whitelist-ultra check <nick>")
            return
        }

        val nick = args[0]

        val result = CheckCommand.execute(nick)

        sender.sendMessage(result)
    }
}

class ListBungee : Command("whitelistlist") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        val result = ListCommand.execute(dirWL)
        sender.sendMessage(result)
    }
}

//class ChangeBungee : Command("whitelistchange") {
//    override fun execute(sender: CommandSender, args: Array<out String>) {
//        if (args.size < 2) {
//            sender.sendMessage(WLUCore.gT("commands.usage") + " /whitelist-ultra change <old> <new>")
//            return
//        }
//
//        val result = ChangeCommand(args[0], args[1], dirWL)
//        sender.sendMessage(result)
//    }
//}


class RemoveBungee : Command("whitelistremove") {

    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.size < 1) {
            sender.sendMessage(WLUCore.gT("commands.usage") + " /whitelist-ultra del <player>")
            return
        }

        val nick = args[0]
        val result = RemoveCommand.removeFromWhitelist(nick)

        if (result) {
            sender.sendMessage(WLUCore.gT("commands.remove.m1"))
            val player = net.md_5.bungee.api.ProxyServer.getInstance().getPlayer(nick)
            if (player != null && player.isConnected) {
                player.disconnect(WLUCore.gT("commands.remove.kick"))
            }
        } else {
            sender.sendMessage(WLUCore.gT("commands.remove.m1"))
        }
    }
}

class ReloadBungee : Command("whitelistreload") {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        sender.sendMessage(ReloadCommand.reload())
    }
}
