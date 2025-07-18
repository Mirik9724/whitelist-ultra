package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.Mirik9724.whitelist_ultra.WLUCore.gT
import java.io.File
import java.io.IOException

object ListCommand {

    fun execute(file: File): String {
        val whitelistPlayers = StringBuilder(gT("commands.list"))

        return try {
            val mapper = ObjectMapper()

            if (file.exists()) {
                val rootNode: JsonNode = mapper.readTree(file)

                if (rootNode.isEmpty) {
                    "Whitelist is empty."
                } else {
                    rootNode.forEach { node ->
                        whitelistPlayers.append(node.asText()).append(" ")
                    }
                    whitelistPlayers.toString().trim()
                }
            } else {
                "Whitelist file does not exist."
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Error reading whitelist: ${e.message}"
        }
    }
}
