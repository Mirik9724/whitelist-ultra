package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import net.Mirik9724.whitelist_ultra.WLUCore
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL

object ChangeCommand {
    fun execute(oldNick: String, newNick: String): String {
        val mapper = ObjectMapper()

        val rootNode: JsonNode = mapper.readTree(dirWL)
        if (!rootNode.isArray) {
            return "Invalid file format."
        }

        val arrayNode = rootNode as ArrayNode
        val index = arrayNode.indexOf(arrayNode.find { it.asText() == oldNick })

        if (index == -1) {
            return WLUCore.gT("commands.change.old_not_found")
        }

        if (arrayNode.any { it.asText() == newNick }) {
            return WLUCore.gT("commands.change.duplicate")
        }

        arrayNode.set(index, mapper.convertValue(newNick, JsonNode::class.java))
        mapper.writeValue(dirWL, arrayNode)

        return WLUCore.gT("commands.change.success")
    }
}
