package net.Mirik9724.whitelist_ultra.Bukkit

import net.Mirik9724.api.bstats.bukkit.Metrics
import net.Mirik9724.api.log
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.data
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

private val modernVersion: Boolean by lazy {
    val versionString = Bukkit.getBukkitVersion().substringBefore("-")
    val major = versionString.substringBefore(".").toIntOrNull() ?: 1
    val minor = versionString.substringAfter(".").substringBefore(".").toIntOrNull() ?: 0
    major > 1 || (major == 1 && minor >= 16)
}
fun isModern(): Boolean = modernVersion

class WLUBu : JavaPlugin() {

    override fun onEnable() {
        val commandExecutor = WLUCommand()
        getCommand("whitelist-ultra")?.setExecutor(commandExecutor)

        WLUCore
        server.pluginManager.registerEvents(BukkitPlayerLoginListener(this), this)
        if(data["use-metric"] == "true") {
            Metrics(this, 24668)
        }

        log.info("WLU has loaded")
    }


    override fun onDisable() {
        log.info("Whitelist Ultra has unloaded")
    }
}


