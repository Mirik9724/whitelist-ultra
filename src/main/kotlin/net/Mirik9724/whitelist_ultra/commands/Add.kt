package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import net.Mirik9724.whitelist_ultra.WLU
import net.Mirik9724.whitelist_ultra.WLU.Companion.whitelist_f
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class Add(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLU.instance.gT("commands.usage") + " /whitelist-ultra add <player>")
            return false
        }

        WLU.nick = args[1]

        try {
            val mapper = ObjectMapper()
            val file = File(plugin.dataFolder, whitelist_f)

            // If the file doesn't exist, create a new one
            val arrayNode: ArrayNode = if (file.exists()) {
                // Read the existing JSON file
                val rootNode: JsonNode = mapper.readTree(file)
                if (rootNode.isArray) {
                    rootNode as ArrayNode
                } else {
                    sender.sendMessage("The file contains an incorrect format. A new whitelist is created.")
                    mapper.createArrayNode()
                }
            } else {
                // Create a new JSON array if the file doesn't exist
                mapper.createArrayNode()
            }

            // Check if the player is already in the list
            val exists = arrayNode.any { it.asText() == WLU.nick }

            if (!exists) {
                arrayNode.add(WLU.nick)
                mapper.writeValue(file, arrayNode) // Write the updated array to the file
                sender.sendMessage(WLU.instance.gT("commands.add.m1"))
            } else {
                sender.sendMessage(WLU.instance.gT("commands.add.m2"))
            }

        } catch (e: IOException) {
            sender.sendMessage("Error when adding a player to the whitelist: ${e.message}")
            e.printStackTrace()
        }

        return true
    }
}
