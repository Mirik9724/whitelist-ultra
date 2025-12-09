package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL
import net.Mirik9724.whitelist_ultra.WLUCore.gT
import java.io.File
import java.io.IOException

object CheckCommand {

    fun execute(nick: String): String {
        return try {
            val mapper = ObjectMapper()

            if (!dirWL.exists()) {
                return "Whitelist file not found."
            }

            val rootNode: JsonNode = mapper.readTree(dirWL)
            if (!rootNode.isArray) {
                return "The file contains an incorrect format. Expecting an array."
            }

            val arrayNode = rootNode as ArrayNode
            val found = arrayNode.any { it.asText().equals(nick, ignoreCase = true) }

            if (found) gT("commands.check.found").toString()
            else gT("commands.check.not_found").toString()

        } catch (e: IOException) {
            "Error when checking whitelist: ${e.message}"
        }
    }
}
