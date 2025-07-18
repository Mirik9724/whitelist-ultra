package net.Mirik9724.whitelist_ultra.Ve

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.ProxyServer
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL
import net.Mirik9724.whitelist_ultra.commands.*
import net.kyori.adventure.text.Component

class AddVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val source = invocation.source()
        val args = invocation.arguments()

        if (args.isEmpty()) {
            source.sendMessage(Component.text("Usage: /whitelist-ultra add <nick>"))
            return
        }

        val nick = args[0]
        val result = AddCommand.execute(nick)

        source.sendMessage(Component.text(result))
    }
}

class CheckVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val source = invocation.source()
        val args = invocation.arguments()

        if (args.isEmpty()) {
            source.sendMessage(Component.text("Usage: /whitelist-ultra check <nick>"))
            return
        }

        val nick = args[0]
        val result = CheckCommand.execute(nick)

        source.sendMessage(Component.text(result))
    }
}

class ListVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val result = ListCommand.execute(dirWL)
        invocation.source().sendMessage(Component.text(result))
    }
}

class RemoveVelocity(private val server: ProxyServer) : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        val args = invocation.arguments()
        if (args.isEmpty()) {
            invocation.source().sendMessage(Component.text(WLUCore.gT("commands.usage") + " /whitelist-ultra del <player>"))
            return
        }

        val nick = args[0]
        val result = RemoveCommand.removeFromWhitelist(nick)

        if (result) {
            invocation.source().sendMessage(Component.text(WLUCore.gT("commands.remove.m1")))
            val player = server.getPlayer(nick)
            if (player.isPresent) {
                player.get().disconnect(Component.text(WLUCore.gT("commands.remove.kick")))
            }
        } else {
            invocation.source().sendMessage(Component.text(WLUCore.gT("commands.remove.m1")))
        }
    }
}

class ChangeVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val args = invocation.arguments()
        val source = invocation.source()

        if (args.size < 2) {
            source.sendMessage(Component.text("Usage: /whitelist-ultra change <old> <new>"))
            return
        }

        val result = ChangeCommand.execute(args[0], args[1])
        source.sendMessage(Component.text(result))
    }
}


class ReloadVelocity : SimpleCommand {

    override fun execute(invocation: SimpleCommand.Invocation) {
        invocation.source().sendMessage(Component.text(ReloadCommand.reload()))
    }
}
