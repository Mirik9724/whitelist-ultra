package net.Mirik9724.whitelist_ultra.Ve

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.logger
import net.Mirik9724.whitelist_ultra.WLUCore.whitelist_f
import net.Mirik9724.whitelist_ultra.getAllowedPlayers
import java.nio.file.Path

class VelocityPlayerLoginListener(
    private val server: ProxyServer,
    private val plugin: PluginContainer,
    private val dataDirectory: Path // 👈 это путь до папки плагина, один раз передаётся
) {

    @Subscribe
    fun onLogin(event: LoginEvent) {
        val playerName = event.player.username
        val whitelistFile = dataDirectory.resolve(whitelist_f).toFile() // ✅ без plugin.dataFolder
        val allowedPlayers = getAllowedPlayers(whitelistFile)

        logger.info("Checking whitelist for player: $playerName")

        if (!allowedPlayers.contains(playerName.trim())) {
            val kickMessage = Component.text(WLUCore.gT("kick"))
            event.result = ResultedEvent.ComponentResult.denied(kickMessage)
        }
    }
}
