package net.Mirik9724.whitelist_ultra.Sp

import net.Mirik9724.whitelist_ultra.getAllowedPlayers
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.whitelist_f
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SpigotPlayerLoginListener(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun onPlayerLoginSp(event: PlayerLoginEvent) {
        val playerName = event.player.name
        val allowedPlayers = getAllowedPlayers(File(plugin.dataFolder, whitelist_f))

        plugin.logger.info("Checking whitelist for player: $playerName")

        if (!allowedPlayers.map { it.trim() }.contains(playerName.trim())) {
            val kickMessage = WLUCore.gT("kick")
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, kickMessage.toString())
        }
    }
}
