package net.Mirik9724.whitelist_ultra.Velocity

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import net.Mirik9724.api.toMM
import net.Mirik9724.api.log
import net.Mirik9724.whitelist_ultra.Commands.wld
import net.Mirik9724.whitelist_ultra.WLUCore.gT

class VelocityPlayerLoginListener() {
    @Subscribe
    fun onLogin(event: LoginEvent) {
        val playerName = event.player.username

        log.info(wld.toString())
        if (!wld.any { it.asText().trim() == playerName }) {
            event.result = ResultedEvent.ComponentResult.denied(toMM(gT("kick")))
        }
    }
}
