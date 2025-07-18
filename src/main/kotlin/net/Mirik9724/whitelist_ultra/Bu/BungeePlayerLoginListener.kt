package net.Mirik9724.whitelist_ultra.Bu

import net.md_5.bungee.api.connection.PendingConnection
import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.api.plugin.Listener
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.dataFolder
import net.Mirik9724.whitelist_ultra.WLUCore.whitelist_f
import net.Mirik9724.whitelist_ultra.getAllowedPlayers
import java.io.File

class BungeePlayerLoginListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onLogin(event: LoginEvent) {
        val connection: PendingConnection = event.connection
        val playerName = connection.name
        val allowedPlayers = getAllowedPlayers(File(dataFolder, whitelist_f))

        plugin.logger.info("Checking whitelist for player: $playerName")

        if (!allowedPlayers.map { it.trim() }.contains(playerName.trim())) {
            val kickMessage = WLUCore.gT("kick")
            event.setCancelled(true)
            event.setCancelReason(net.md_5.bungee.api.chat.TextComponent(kickMessage))
        }
    }
}
