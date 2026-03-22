package net.Mirik9724.whitelist_ultra.Velocity

import com.velocitypowered.api.command.CommandMeta
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.plugin.Dependency
import net.Mirik9724.whitelist_ultra.Commands.wld
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.data
import net.Mirik9724.whitelist_ultra.WLUCore.log
import net.Mirik9724.whitelist_ultra.vers
import java.nio.file.Path
import com.google.inject.Inject
import net.Mirik9724.api.bstats.velocity.Metrics

@Plugin(id = "whitelist-ultra",
    name = "WhitelistUltra",
    version = vers,
    authors = ["Mirik9724"],
    dependencies = [
        Dependency(id = "mirikapi")
    ]
)
class WLUVe @Inject
constructor(
    private val server: ProxyServer,
    @DataDirectory private val dataDirectory: Path,
    private val pluginContainer: PluginContainer,
    private val metricsFactory: Metrics.Factory
) {

    private val subCommands: MutableMap<String, SimpleCommand> = HashMap()
    companion object {
        var customnamesforgeysermcInstaled = false
        var miloginInstaled = false
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        WLUCore

        subCommands["add"] = AddVelocity()
        subCommands["list"] = ListVelocity()
        subCommands["remove"] = RemoveVelocity(server)
        subCommands["change"] = ChangeVelocity()
        subCommands["del"] = RemoveVelocity(server)
        subCommands["reload"] = ReloadVelocity()
        subCommands["check"] = CheckVelocity()


        val meta: CommandMeta = server.commandManager.metaBuilder("whitelist-ultra")
            .aliases("wlu")
            .plugin(pluginContainer)
            .build()

        server.commandManager.register(meta, MainCommand())

        server.eventManager.register(
            pluginContainer,
            VelocityPlayerLoginListener()
        )

        if(data["use-metric"] == "true") {
//            Metrics(this, server, log, dataDirectory, 28855)

            metricsFactory.make(this, 28855);


//            Metrics.Factory(server, log, dataDirectory)
//                .make(this, 28855)

//            net.Mirik9724.api.bstats.velocity.Metrics::class.java
//                .getDeclaredConstructor(
//                    Any::class.java,
//                    ProxyServer::class.java,
//                    logger::class.java,
//                    Path::class.java,
//                    Int::class.java
//                )
//                .apply { isAccessible = true }
//                .newInstance(this, server, logger, dataDirectory, 28855) as net.Mirik9724.api.bstats.velocity.Metrics
        }

        customnamesforgeysermcInstaled = server.pluginManager.getPlugin("customnamesforgeysermc").isPresent
        miloginInstaled = server.pluginManager.getPlugin("milogin").isPresent

        log.info("WLU has loaded")
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
                    WLUCore.gT("commands.error.underknewcom").toString().replace("@undsubcom", args[0])
                ))
            }
        }

        override fun suggest(invocation: SimpleCommand.Invocation): MutableList<String> {
            val args = invocation.arguments()
            if (args.isEmpty()) return subCommands.keys.toMutableList()

            if (args.size == 1) {
                val input = args[0].lowercase()
                return subCommands.keys
                    .filter { it.startsWith(input) }
                    .toMutableList()
            }

            if (args.size == 2) {
                val sub = args[0].lowercase()
                if (sub == "remove" || sub == "del" || sub == "check" || sub == "change") {
                    val input = args[1].lowercase()
                    val whitelistPlayers = wld.map { it.asText() }
                    return whitelistPlayers
                        .filter { it.lowercase().startsWith(input) }
                        .toMutableList()
                }
            }

            return mutableListOf()
        }
    }
}
