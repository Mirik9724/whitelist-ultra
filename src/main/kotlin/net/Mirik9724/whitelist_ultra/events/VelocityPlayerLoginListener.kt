package net.Mirik9724.whitelist_ultra.events

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.Mirik9724.whitelist_ultra.WLU

class VelocityPlayerLoginListener(
    private val server: ProxyServer,
    private val plugin: PluginContainer
) {

    @Subscribe
    fun onLogin(event: LoginEvent) {
        val playerName = event.player.username
        val allowedPlayers = getAllowedPlayers(WLU.instance) // Получаем вайтлист

        WLU.instance.logger.info("Checking whitelist for player: $playerName")

        if (!allowedPlayers.contains(playerName.trim())) {
            val kickMessage = Component.text(WLU.instance.gT("kick"))
            event.setResult(ResultedEvent.ComponentResult.denied(kickMessage))
        }
    }
}
