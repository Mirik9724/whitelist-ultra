package net.Mirik9724.whitelist_ultra.events

import net.md_5.bungee.api.connection.PendingConnection
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.api.plugin.Listener
import net.Mirik9724.whitelist_ultra.WLU

class BungeePlayerLoginListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onLogin(event: LoginEvent) {
        val connection: PendingConnection = event.connection
        val playerName = connection.name
        val allowedPlayers = getAllowedPlayers(WLU.instance)

        plugin.logger.info("Checking whitelist for player: $playerName")

        if (!allowedPlayers.map { it.trim() }.contains(playerName.trim())) {
            val kickMessage = WLU.instance.gT("kick")
            event.setCancelled(true)
            event.setCancelReason(net.md_5.bungee.api.chat.TextComponent(kickMessage))
        }
    }
}
