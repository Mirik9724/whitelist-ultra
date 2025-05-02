package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.Mirik9724.whitelist_ultra.WLU
import net.Mirik9724.whitelist_ultra.WLU.Companion.whitelist_f
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class List(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Logic for displaying the whitelist players
        val whitelistPlayers = StringBuilder(WLU.instance.gT("commands.list"))

        try {
            val mapper = ObjectMapper()
            val file = File(plugin.dataFolder, whitelist_f) // Ensure the path is correct

            if (file.exists()) {
                // Read the JSON file
                val rootNode: JsonNode = mapper.readTree(file)

                if (rootNode.isEmpty) {
                    sender.sendMessage("JSON file is empty.")
                } else {
                    // Collect player names into a string
                    rootNode.forEach { node ->
                        whitelistPlayers.append(node.asText()).append(" ")
                    }

                    // Send information to the user
                    sender.sendMessage(whitelistPlayers.toString().trim()) // Remove extra space at the end
                }
            } else {
                sender.sendMessage("The file does not exist.")
            }

        } catch (e: IOException) {
            sender.sendMessage("Error reading file: ${e.message}")
            e.printStackTrace()
        }

        return true
    }
}
