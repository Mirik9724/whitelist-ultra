package net.Mirik9724.whitelist_ultra

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.Mirik9724.whitelist_ultra.WLUCore.log
import java.io.File

fun getAllowedPlayers(file: File): List<String> {
    if (!file.exists()) {
        log.warn("Whitelist file not found, returning an empty list.")
        return emptyList()
    }

    return try {
        val mapper = ObjectMapper()
        val rootNode: JsonNode = mapper.readTree(file)
        if (rootNode.isArray) {
            rootNode.map { it.asText().trim() }.filter { it.isNotEmpty() }
        } else {
            log.warn("Invalid format in whitelist file. Expected an array.")
            emptyList()
        }
    } catch (e: Exception) {
        log.error("Error reading whitelist file: ${e.message}")
        emptyList()
    }
}
