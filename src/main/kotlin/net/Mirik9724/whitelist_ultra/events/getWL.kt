package net.Mirik9724.whitelist_ultra.events

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.Mirik9724.whitelist_ultra.WLU.Companion.whitelist_f
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

fun getAllowedPlayers(plugin: JavaPlugin): List<String> {
    val file = File(plugin.dataFolder, whitelist_f)
    if (!file.exists()) {
        plugin.logger.warning("Whitelist file not found, returning an empty list.")
        return emptyList()
    }

    return try {
        val mapper = ObjectMapper()
        val rootNode: JsonNode = mapper.readTree(file)
        if (rootNode.isArray) {
            rootNode.map { it.asText().trim() }.filter { it.isNotEmpty() }
        } else {
            plugin.logger.warning("Invalid format in whitelist file. Expected an array.")
            emptyList()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}