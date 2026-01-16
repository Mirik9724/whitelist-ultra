package net.Mirik9724.whitelist_ultra.Velocity

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.Commands.Add
import net.Mirik9724.whitelist_ultra.Commands.Del
import net.Mirik9724.whitelist_ultra.Commands.Check
import net.Mirik9724.whitelist_ultra.Commands.Change
import net.Mirik9724.whitelist_ultra.Commands.List
import net.Mirik9724.whitelist_ultra.Commands.Reload
import net.Mirik9724.whitelist_ultra.WLUCore.udhp
import net.kyori.adventure.text.Component

class AddVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val source = invocation.source()
        val args = invocation.arguments()
        if (!source.hasPermission("whitelist-ultra.add") && !invocation.source().hasPermission("whitelist-ultra.admin")) {
            source.sendMessage(Component.text(udhp))
            return
        }

        if (args.isEmpty()) {
            source.sendMessage(Component.text("Usage: /wlu add <nick>"))
            return
        }
        WLUCore.nick = args[1]
        source.sendMessage(Component.text(Add(args[1])))
    }
}

class CheckVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        val source = invocation.source()
        val args = invocation.arguments()
        if (!source.hasPermission("whitelist-ultra.check") && !invocation.source().hasPermission("whitelist-ultra.admin")) {
            source.sendMessage(Component.text(udhp))
            return
        }

        if (args.isEmpty()) {
            source.sendMessage(Component.text("Usage: /wlu check <nick>"))
            return
        }
        WLUCore.nick = args[1]
        source.sendMessage(Component.text(Check(args[1])))
    }
}

class ListVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        if (!invocation.source().hasPermission("whitelist-ultra.list") && !invocation.source().hasPermission("whitelist-ultra.admin")) {
            invocation.source().sendMessage(Component.text(udhp))
            return
        }
        invocation.source().sendMessage(Component.text(List()))
    }
}

class RemoveVelocity(private val server: ProxyServer) : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        if (!invocation.source().hasPermission("whitelist-ultra.remove") && !invocation.source().hasPermission("whitelist-ultra.admin")) {
            invocation.source().sendMessage(Component.text(udhp))
            return
        }
        val args = invocation.arguments()
        if (args.isEmpty()) {
            invocation.source()
                .sendMessage(Component.text(WLUCore.gT("commands.usage").toString() + " /wlu del <player>"))
            return
        }
        WLUCore.nick = args[1]
        invocation.source().sendMessage(Component.text(Del(args[1])))
        val player: Player? = server.getPlayer(args[1]).orElse(null)
        player?.let {
            it.disconnect(Component.text(WLUCore.gT("commands.remove.kick")))
        }
    }
}

class ChangeVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        if (!invocation.source().hasPermission("whitelist-ultra.change") && !invocation.source().hasPermission("whitelist-ultra.admin")) {
            invocation.source().sendMessage(Component.text(udhp))
            return
        }
        val args = invocation.arguments()
        val source = invocation.source()
        if (args.size < 2) {
            source.sendMessage(Component.text("Usage: /wlu change <old> <new>"))
            return
        }
        source.sendMessage(Component.text(Change(args[1], args[2])))
    }
}

class ReloadVelocity : SimpleCommand {
    override fun execute(invocation: SimpleCommand.Invocation) {
        if (!invocation.source().hasPermission("whitelist-ultra.reload") && !invocation.source().hasPermission("whitelist-ultra.admin")) {
            invocation.source().sendMessage(Component.text(udhp))
            return
        }
        invocation.source().sendMessage(Component.text(Reload()))
    }
}
