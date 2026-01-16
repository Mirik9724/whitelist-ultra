package net.Mirik9724.whitelist_ultra.Bu

import net.Mirik9724.api.log
import net.Mirik9724.whitelist_ultra.WLUCore
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.CommandSender

class WLUBu : Plugin() {

    private val subCommands: MutableMap<String, Command> = HashMap()

    override fun onEnable() {
        WLUCore

        subCommands["add"] = AddBungee()
        subCommands["list"] = ListBungee()
        subCommands["remove"] = RemoveBungee()
        subCommands["delite"] = RemoveBungee()
        subCommands["del"] = RemoveBungee()
        subCommands["reload"] = ReloadBungee()
        subCommands["check"] = CheckBungee()

        proxy.pluginManager.registerCommand(this, MainCommand())

        log.info("WLU Bungee plugin enabled")
    }

    inner class MainCommand : Command("whitelist-ultra") {
        override fun execute(sender: CommandSender, args: Array<String>) {
            if (args.isEmpty()) {
                sender.sendMessage(WLUCore.gT("commands.subc"))
                return
            }
            val sub = subCommands[args[0].lowercase()]
            if (sub != null) {
                sub.execute(sender, args)
            } else {
                sender.sendMessage(WLUCore.gT("commands.error.underknewcom").replace("@undsubcom", args[0]))
            }
        }
    }
}
