package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL
import net.Mirik9724.whitelist_ultra.WLUCore.gT
import java.io.File

object AddCommand {
    fun execute(nick: String): String {
        val mapper = ObjectMapper()

        // Загружаем или создаём массив
        val arrayNode: ArrayNode = if (dirWL.exists()) {
            val rootNode: JsonNode = mapper.readTree(dirWL)
            if (rootNode.isArray) {
                rootNode as ArrayNode
            } else {
                return "Invalid file format. A new whitelist will be created."
            }
        } else {
            mapper.createArrayNode()
        }

        if (arrayNode.any { it.asText() == nick }) {
            return gT("commands.add.m2")
        }

        arrayNode.add(nick)
        mapper.writeValue(dirWL, arrayNode)
        return gT("commands.add.m1")
    }
}
