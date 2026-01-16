package net.Mirik9724.whitelist_ultra.Bukkit

import net.Mirik9724.whitelist_ultra.WLUCore.gT
import org.bukkit.event.EventHandler
import net.Mirik9724.whitelist_ultra.Commands.wld
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin

class BukkitPlayerLoginListener(private val plugin: JavaPlugin) : Listener {
    @EventHandler
    fun onPlayerLoginSp(event: PlayerLoginEvent) {
        val playerName = event.player.name

        if (!wld.any { it.asText().trim() == playerName }) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, gT("kick"))
        }
    }
}

