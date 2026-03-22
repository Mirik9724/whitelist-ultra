package net.Mirik9724.whitelist_ultra.Velocity

import com.velocitypowered.api.event.ResultedEvent
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.LoginEvent
import net.Mirik9724.api.toMM
import net.Mirik9724.whitelist_ultra.Commands.wld
import net.Mirik9724.whitelist_ultra.Velocity.WLUVe.Companion.miloginInstaled
import net.Mirik9724.whitelist_ultra.WLUCore.gT
import net.Mirik9724.whitelist_ultra.WLUCore.log

class VelocityPlayerLoginListener() {
    companion object {
        lateinit var instance: VelocityPlayerLoginListener
    }
    init {
        instance = this
    }


    fun check(player: String): Boolean {
        if (!wld.any { it.asText().trim() == player }) {
            return false
        }
        else{
            return true
        }
    }

    @Subscribe
    fun onLogin(event: LoginEvent) {
        if(WLUVe.customnamesforgeysermcInstaled or WLUVe.miloginInstaled != true){
            if(check(event.player.username.toString()) == false){
                event.result = ResultedEvent.ComponentResult.denied(toMM(gT("kick")))
            }
        }
    }
}
