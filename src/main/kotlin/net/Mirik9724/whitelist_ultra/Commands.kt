package net.Mirik9724.whitelist_ultra

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import net.Mirik9724.api.loadYmlFile
import net.Mirik9724.whitelist_ultra.WLUCore.conf
import net.Mirik9724.whitelist_ultra.WLUCore.dirWL
import net.Mirik9724.whitelist_ultra.WLUCore.ds
import net.Mirik9724.whitelist_ultra.WLUCore.gT

object Commands {
    lateinit var wld: ArrayNode
    internal fun loadWL(): ArrayNode {
        if (!dirWL.exists()) return mapper.createArrayNode()
        val root = mapper.readTree(dirWL)
        return if (root.isArray) root as ArrayNode else mapper.createArrayNode()
    }

    private fun saveWL(arrayNode: ArrayNode) {
        mapper.writeValue(dirWL, arrayNode)
    }
    val mapper = ObjectMapper()

    fun Add(nick: String): String {
        if (wld.any { it.asText() == nick }) return gT("commands.add.m2")
        wld.add(nick)
        saveWL(wld)
        wld = loadWL()
        return gT("commands.add.m1")
    }

    fun Del(nick: String): String {
        val removed = wld.removeAll { it.asText() == nick }
        return if (removed) {
            saveWL(wld)
            wld = loadWL()
            WLUCore.gT("commands.remove.m1")
        } else {
            WLUCore.gT("commands.remove.m2")
        }
    }

    fun Reload(): String {
        wld = loadWL()
        WLUCore.data = loadYmlFile("plugins/whitelist_ultra/"+ conf)
        WLUCore.dsdata = loadYmlFile("plugins/whitelist_ultra/"+ ds)
        return WLUCore.gT("reload")
    }

    fun List(): String {
        if (wld.isEmpty) return "Whitelist is empty"
        return buildString {
            append(gT("commands.list")).append(" ")
            wld.forEach { append(it.asText()).append(" ") }
        }.trim()
    }

    fun Check(nick: String): String {
        val found = wld.any { it.asText() == nick }
        return if (found) gT("commands.check.found") else gT("commands.check.not_found")
    }

    fun Change(oldNick: String, newNick: String): String {
        val index = wld.indexOfFirst { it.asText() == oldNick }
        if (index == -1) return gT("commands.change.old_not_found")
        if (wld.any { it.asText() == newNick }) return gT("commands.change.duplicate")

        wld.set(index, mapper.convertValue(newNick, JsonNode::class.java))
        saveWL(wld)
        wld = loadWL()
        return gT("commands.change.success")
    }
}