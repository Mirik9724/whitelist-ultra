package net.Mirik9724.whitelist_ultra.Sp

import net.Mirik9724.api.logger_
import org.bukkit.plugin.java.JavaPlugin
import net.Mirik9724.whitelist_ultra.WLUCore

class WLUSp : JavaPlugin() {

    override fun onEnable() {

        val commandExecutor = WLUCommand(this)
        getCommand("whitelist-ultra")?.setExecutor(commandExecutor)

        WLUCore
        server.pluginManager.registerEvents(SpigotPlayerLoginListener(this), this)

        logger_.info("WLU has loaded")
    }


    override fun onDisable() {
        logger_.info("Whitelist Ultra has unloaded")
    }
}


