package net.Mirik9724.whitelist_ultra.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.Mirik9724.whitelist_ultra.WLU
import net.Mirik9724.whitelist_ultra.WLU.Companion.whitelist_f
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class Check(private val plugin: JavaPlugin) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.size < 2) {
            sender.sendMessage(WLU.instance.gT("commands.usage") + " /whitelist-ultra check <player>")
            return false
        }

        WLU.nick = args[1]

        try {
            val mapper = ObjectMapper()
            val file = File(plugin.dataFolder, whitelist_f)

            if (file.exists()) {
                val rootNode: JsonNode = mapper.readTree(file)
                if (rootNode.isArray) {
                    val arrayNode = rootNode as ArrayNode

                    for (node in arrayNode) {
                        if (node.asText().equals(WLU.nick, ignoreCase = true)) {
                            sender.sendMessage(WLU.instance.gT("commands.check.found"))
                            return true
                        }
                    }
                    sender.sendMessage(WLU.instance.gT("commands.check.not_found"))
                } else {
                    sender.sendMessage("The file contains an incorrect format. Expecting an array.")
                }
            } else {
                sender.sendMessage("Whitelist file not found.")
            }

        } catch (e: IOException) {
            sender.sendMessage("Error when checking whitelist: ${e.message}")
            e.printStackTrace()
        }

        return true
    }
}
