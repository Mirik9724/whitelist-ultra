package net.Mirik9724.api.net.Mirik9724.whitelist_ultra.Ds

import net.Mirik9724.whitelist_ultra.Ds.DSListener
import net.Mirik9724.whitelist_ultra.WLUCore.log
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.Commands

class Discord {
    fun startBot(token: String) {
        try {
            val jda = JDABuilder.createLight(token)
                .addEventListeners(DSListener())
                .build()

            jda.awaitReady()
            log.info("Bot started")

            jda.updateCommands().addCommands(
                Commands.slash("whitelist", "Send ticket message")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
            ).queue {
                log.info("Command registered in Discord API!")
            }


        } catch (e: Exception) {
            log.info("❌ Bot error: ${e.message}")
            e.printStackTrace()
        }
    }
}
