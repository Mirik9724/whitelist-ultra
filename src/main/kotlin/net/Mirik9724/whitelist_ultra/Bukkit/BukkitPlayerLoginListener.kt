package net.Mirik9724.whitelist_ultra.Bukkit

import net.Mirik9724.whitelist_ultra.WLUCore.gT
import org.bukkit.event.EventHandler
import net.Mirik9724.whitelist_ultra.Commands.wld
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin

class BukkitPlayerLoginListener(private val plugin: JavaPlugin) : Listener {
//    private val miniMessage = try {
//        MiniMessage.miniMessage()
//    } catch (e: NoClassDefFoundError) {
//        null
//    }

    @EventHandler
    fun onPlayerLoginSp(event: PlayerLoginEvent) {
        val playerName = event.player.name

        if (!wld.any { it.asText().trim() == playerName }) {
//            if (miniMessage != null) {
//                val formattedKickMessage = miniMessage.deserialize(gT("kick"))
//                event.player.kick(formattedKickMessage)
//            }
//            else {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, gT("kick"))
//            }
        }
    }
}

