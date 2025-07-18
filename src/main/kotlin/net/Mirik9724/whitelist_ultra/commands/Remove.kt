package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL
import java.io.File
import java.io.IOException

object RemoveCommand {

    fun removeFromWhitelist(nick: String): Boolean {
        try {
            val mapper = ObjectMapper()

            if (!dirWL.exists()) return false

            val rootNode = mapper.readTree(dirWL)
            if (!rootNode.isArray) return false

            val arrayNode = rootNode as ArrayNode
            val newArrayNode = mapper.createArrayNode()

            var found = false
            for (node in arrayNode) {
                if (!node.asText().equals(nick, ignoreCase = true)) {
                    newArrayNode.add(node)
                } else {
                    found = true
                }
            }

            if (found) {
                mapper.writeValue(dirWL, newArrayNode)
            }

            return found
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}
