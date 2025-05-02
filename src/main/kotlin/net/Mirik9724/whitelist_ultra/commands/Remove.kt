package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import com.fasterxml.jackson.databind.ObjectMapper
import net.Mirik9724.whitelist_ultra.WLU
import net.Mirik9724.whitelist_ultra.WLU.Companion.whitelist_f
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class Remove(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLU.instance.gT("commands.usage") + " /whitelist-ultra remove <player>")
            return false
        }

        WLU.nick = args[1]

        try {
            val mapper = ObjectMapper()
            val file = File(plugin.dataFolder, whitelist_f)

            if (file.exists()) {
                val rootNode = mapper.readTree(file)
                if (rootNode.isArray) {
                    val arrayNode = rootNode as ArrayNode

                    // Create a new array excluding the player
                    val newArrayNode = mapper.createArrayNode()
                    var found = false

                    for (node in arrayNode) {
                        if (node.asText().equals(WLU.nick, ignoreCase = true).not()) {
                            newArrayNode.add(node)
                        } else {
                            found = true
                        }
                    }

                    if (found) {
                        // Write the updated array back to the file
                        mapper.writeValue(file, newArrayNode)
                        sender.sendMessage(WLU.instance.gT("commands.remove.m1"))
                    } else {
                        sender.sendMessage(WLU.instance.gT("commands.remove.m1"))
                    }

                    // Check if the player is on the server
                    val player = Bukkit.getPlayerExact(WLU.nick)
                    if (player != null && player.isOnline) {
                        player.kickPlayer(WLU.instance.gT("commands.remove.kick"))
                    }
                } else {
                    sender.sendMessage("The file contains an incorrect format. Expecting an array.")
                }
            } else {
                sender.sendMessage("Whitelist file not found.")
            }

        } catch (e: IOException) {
            sender.sendMessage("Error when removing a player from the whitelist: " + e.message)
            e.printStackTrace()
        }

        return true
    }
}
