package net.Mirik9724.whitelist_ultra.Ve

import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import net.Mirik9724.api.logger_
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.vers
import java.nio.file.Path
import javax.inject.Inject

@Plugin(id = "whitelist-ultra", name = "WhitelistUltra", version = vers, authors = ["Mirik9724"])
class WLUVe @Inject constructor( // <--- ВАЖНО: аннотация @Inject
    private val server: ProxyServer,
    @DataDirectory private val dataDirectory: Path,
    private val pluginContainer: PluginContainer,
) {

    private val subCommands: MutableMap<String, SimpleCommand> = HashMap()

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        WLUCore

        subCommands["add"] = AddVelocity()
        subCommands["list"] = ListVelocity()
        subCommands["remove"] = RemoveVelocity(server)
        subCommands["del"] = RemoveVelocity(server)
        subCommands["delite"] = RemoveVelocity(server)
        subCommands["reload"] = ReloadVelocity()
        subCommands["check"] = CheckVelocity()


        server.commandManager.register("whitelist-ultra", MainCommand(), "wlu")

        server.eventManager.register(
            pluginContainer,
            VelocityPlayerLoginListener(server, pluginContainer, dataDirectory)
        )

        logger_.info("WLU Velocity plugin enabled")
    }

    inner class MainCommand : SimpleCommand {
        override fun execute(invocation: SimpleCommand.Invocation) {
            val args = invocation.arguments()
            if (args.isEmpty()) {
                invocation.source().sendMessage(net.kyori.adventure.text.Component.text(WLUCore.gT("commands.subc")))
                return
            }

            val sub = subCommands[args[0].lowercase()]
            if (sub != null) {
                sub.execute(invocation)
            } else {
                invocation.source().sendMessage(net.kyori.adventure.text.Component.text(
                    WLUCore.gT("commands.error.underknewcom").replace("@undsubcom", args[0])
                ))
            }
        }
    }
}
