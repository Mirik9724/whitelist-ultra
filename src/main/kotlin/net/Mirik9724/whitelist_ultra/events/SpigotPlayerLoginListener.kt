package net.Mirik9724.whitelist_ultra.events

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.Mirik9724.whitelist_ultra.WLU
import net.Mirik9724.whitelist_ultra.WLU.Companion.whitelist_f
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SpigotPlayerLoginListener(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun onPlayerLoginSp(event: PlayerLoginEvent) {
        val playerName = event.player.name
        val allowedPlayers = getAllowedPlayers(plugin)

        plugin.logger.info("Checking whitelist for player: $playerName")

        if (!allowedPlayers.map { it.trim() }.contains(playerName.trim())) {
            val kickMessage = WLU.instance.gT("kick")
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, kickMessage)
        }
    }
}
