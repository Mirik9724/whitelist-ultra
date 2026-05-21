package net.Mirik9724.whitelist_ultra.Ds

import net.Mirik9724.whitelist_ultra.Commands.Add
import net.Mirik9724.whitelist_ultra.WLUCore.dsdata
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color
import java.util.concurrent.Executors
import java.util.EnumSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class DSListener : ListenerAdapter() {

    private val deletionTasks = ConcurrentHashMap<String, ScheduledFuture<*>>()

    private fun getActiveQuestions(): List<Pair<String, String>> {
        val questions = mutableListOf<Pair<String, String>>()
        for (i in 1..5) {
            val question = dsdata["modal.q$i"]
            if (question.isNullOrBlank()) continue

            val rawPlaceholder = dsdata["modal.a$i"]
            val placeholder = if (rawPlaceholder.isNullOrBlank()) "Type your answer..." else rawPlaceholder

            questions.add(Pair(question, placeholder))
        }
        return questions
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val title = dsdata["embedTitle"] ?: "Server Whitelist Application"
        val description = dsdata["embedDescription"] ?: "Click the button below to open a ticket and apply for the whitelist."
        val colorHex = dsdata["embedColor"] ?: "#5865F2"
        val btnText = dsdata["buttonText"] ?: "Open Ticket"

        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(Color.decode(colorHex))
            .build()

        val startButton = Button.primary("wlu_btn_start_ticket", btnText)

        event.replyEmbeds(embed)
            .addActionRow(startButton)
            .queue()
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val guild = event.guild ?: return
        val channel = event.channel.asTextChannel()

        if (event.componentId == "wlu_btn_start_ticket") {
            val activeQuestions = getActiveQuestions()

            if (activeQuestions.isEmpty()) {
                event.reply("❌ Error: Whitelist questions are not configured in dsdata (q1, q2 missing).")
                    .setEphemeral(true).queue()
                return
            }

            val actionRows = mutableListOf<ActionRow>()

            activeQuestions.forEachIndexed { index, pair ->
                val question = pair.first
                val placeholder = pair.second
                val labelText = if (question.length > 45) question.take(42) + "..." else question

                val inputField = TextInput.create("f_$index", labelText, TextInputStyle.SHORT)
                    .setPlaceholder(placeholder)
                    .setRequired(true)
                    .build()

                actionRows.add(ActionRow.of(inputField))
            }

            val modal = Modal.create("wlu_modal", "Заявка в Вайтлист")
                .addComponents(actionRows)
                .build()

            event.replyModal(modal).queue()
            return
        }

        val modRoleId = dsdata["moderatorRoleId"]
        val hasPermission = if (!modRoleId.isNullOrBlank()) {
            event.member?.roles?.any { it.id == modRoleId } == true
        } else {
            event.member?.hasPermission(Permission.MANAGE_SERVER) == true
        }

        if (!hasPermission) {
            event.reply("❌ Error: You do not have permission to use these buttons.").setEphemeral(true).queue()
            return
        }

        val topic = channel.topic ?: ""
        val targetUserId: String
        val targetNick: String

        if (topic.startsWith("WLU-Ticket:") && topic.contains("||")) {
            val dataRaw = topic.substringAfter("WLU-Ticket:")
            targetUserId = dataRaw.substringBefore("||").trim()
            targetNick = dataRaw.substringAfter("||").trim()
        } else if (topic.startsWith("WLU-Ticket for user:")) {
            targetUserId = topic.substringAfter("WLU-Ticket for user:").trim()
            targetNick = channel.name.substringAfter("ticket-").trim()
        } else {
            event.reply("❌ Error: Invalid ticket format in channel topic.").setEphemeral(true).queue()
            return
        }

        when (event.componentId) {
            "wlu_btn_approve" -> {
                event.deferEdit().queue()

                guild.retrieveMemberById(targetUserId).queue({ member ->

                    if (!guild.selfMember.canInteract(member)) {
                        channel.sendMessage("⚠️ **Cannot manage this user!** Their role is higher than the bot's.").queue()
                        approvePlayerInGame(targetNick, targetUserId)
                        channel.sendMessage("🚀 Game database updated for **$targetNick**.").queue()
                        return@queue
                    }

                    val wlRoleId = dsdata["whitelistRoleId"] ?: dsdata["moderatorRoleId"]
                    if (!wlRoleId.isNullOrBlank()) {
                        val role = guild.getRoleById(wlRoleId)
                        if (role != null) {
                            guild.addRoleToMember(member, role).queue(
                                { channel.sendMessage("✅ Role **${role.name}** has been added to ${member.asMention}.").queue() },
                                { error ->
                                    if (error is net.dv8tion.jda.api.exceptions.HierarchyException) {
                                        channel.sendMessage("✅ Role **${role.name}** processed (Hierarchy warning skipped).").queue()
                                    } else {
                                        channel.sendMessage("⚠️ Failed to add role: ${error.message}").queue()
                                    }
                                }
                            )
                        }
                    }

                    member.modifyNickname(targetNick).queue(
                        { channel.sendMessage("✅ Nickname changed to **$targetNick** for ${member.asMention}.").queue() },
                        { error -> channel.sendMessage("⚠️ Cannot change nickname: ${error.message}").queue() }
                    )

                    approvePlayerInGame(targetNick, targetUserId)
                    channel.sendMessage("🚀 Game database updated for **$targetNick**.").queue()

                }, {
                    channel.sendMessage("⚠️ Could not find user in this guild.").queue()
                })

                val approvedEmbed = EmbedBuilder(event.message.embeds.firstOrNull() ?: return)
                    .setColor(Color.BLUE)
                    .setTitle("Application Approved (Deleting in 1 min...)")
                    .build()

                val btnRestore = Button.primary("wlu_btn_restore", "Restore / Undo")

                event.hook.editMessageEmbedsById(event.messageId, approvedEmbed)
                    .setComponents(ActionRow.of(btnRestore)) // Оставляем кнопку возврата
                    .queue()

                channel.sendMessage("✅ **Application approved by ${event.user.asMention}!**\nThis channel will be automatically deleted in **1 minute**. Click **Restore** to cancel.").queue()

                val scheduler = Executors.newSingleThreadScheduledExecutor()
                val scheduledTask = scheduler.schedule({
                    channel.delete().queue({ scheduler.shutdown() }, { scheduler.shutdown() })
                    deletionTasks.remove(channel.id)
                }, 1, TimeUnit.MINUTES)

                deletionTasks[channel.id] = scheduledTask
            }

            "wlu_btn_deny" -> {
                event.deferEdit().queue()

                val deniedEmbed = EmbedBuilder(event.message.embeds.firstOrNull() ?: return)
                    .setColor(Color.RED)
                    .setTitle("Application Denied (Deleting in 1 min...)")
                    .build()

                val btnRestore = Button.primary("wlu_btn_restore", "Restore / Undo")

                event.hook.editMessageEmbedsById(event.messageId, deniedEmbed)
                    .setComponents(ActionRow.of(btnRestore))
                    .queue()

                channel.sendMessage("❌ **Application denied by ${event.user.asMention}!**\nThis channel will be automatically deleted in **1 minute**. Click **Restore** to cancel.").queue()

                val scheduler = Executors.newSingleThreadScheduledExecutor()
                val scheduledTask = scheduler.schedule({
                    channel.delete().queue({ scheduler.shutdown() }, { scheduler.shutdown() })
                    deletionTasks.remove(channel.id)
                }, 1, TimeUnit.MINUTES)

                deletionTasks[channel.id] = scheduledTask
            }

            "wlu_btn_restore" -> {
                event.deferEdit().queue()

                val currentTask = deletionTasks[channel.id]
                if (currentTask != null) {
                    currentTask.cancel(false)
                    deletionTasks.remove(channel.id)

                    val activeEmbed = EmbedBuilder(event.message.embeds.firstOrNull() ?: return)
                        .setColor(Color.GREEN)
                        .setTitle("New Whitelist Application")
                        .build()

                    val btnApprove = Button.success("wlu_btn_approve", "Approve")
                    val btnDeny = Button.danger("wlu_btn_deny", "Deny")

                    event.hook.editMessageEmbedsById(event.messageId, activeEmbed)
                        .setComponents(ActionRow.of(btnApprove, btnDeny))
                        .queue()

                    channel.sendMessage("🔄 **Deletion cancelled by ${event.user.asMention}.** Application is active again.").queue()
                } else {
                    channel.sendMessage("⚠️ Error: Deletion task not found or already executed.").queue()
                }
            }
        }
    }


    override fun onModalInteraction(event: ModalInteractionEvent) {
        if (event.modalId == "wlu_modal") {
            val user = event.user
            val guild = event.guild ?: return

            val memberCreator = event.member ?: return

            var channelNameTemplate = dsdata["namesOfTickets"] ?: "@q1-@q2"
            val activeQuestions = getActiveQuestions()
            val answersMap = mutableMapOf<String, String>()

            activeQuestions.forEachIndexed { index, _ ->
                val answer = event.getValue("f_$index")?.asString ?: "unknown"
                val configKey = "q${index + 1}"
                answersMap[configKey] = answer
            }

            val playerNick = answersMap["q1"] ?: "Unknown"

            for (i in 1..5) {
                val replacement = answersMap["q$i"] ?: ""
                channelNameTemplate = channelNameTemplate.replace("@q$i", replacement)
            }

            val finalChannelName = channelNameTemplate
                .lowercase()
                .replace(Regex("[^a-z0-9\\-_]"), "-")
                .replace(Regex("-+"), "-")
                .trim('-')

            val safeChannelName = if (finalChannelName.isBlank()) "ticket-${user.name.lowercase()}" else finalChannelName

            val categoryId = dsdata["ticketCategoryId"]
            val category = if (!categoryId.isNullOrBlank()) guild.getCategoryById(categoryId) else null

            val modRoleId = dsdata["moderatorRoleId"]
            val modRole = if (!modRoleId.isNullOrBlank()) guild.getRoleById(modRoleId) else null

            var channelAction = guild.createTextChannel(safeChannelName, category)
                .setTopic("WLU-Ticket:${user.id}||$playerNick")
                .addPermissionOverride(guild.publicRole, null, EnumSet.of(Permission.VIEW_CHANNEL)) // Скрываем от всех
                .addPermissionOverride(guild.selfMember, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null) // Доступ боту
                .addPermissionOverride(memberCreator, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null) // ИСПРАВЛЕНО: Передаем Member

            if (modRole != null) {
                channelAction = channelAction.addPermissionOverride(modRole, EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), null)
            }

            channelAction.queue({ ticketChannel ->

                val embed = EmbedBuilder()
                    .setTitle("New Whitelist Application")
                    .setColor(Color.GREEN)
                    .addField("User:", user.asMention, false)

                activeQuestions.forEachIndexed { index, pair ->
                    val question = pair.first
                    val answer = event.getValue("f_$index")?.asString ?: "No answer"
                    embed.addField(question, answer, false)
                }

                embed.setFooter("User ID: ${user.id}")

                val btnApprove = Button.success("wlu_btn_approve", "Approve")
                val btnDeny = Button.danger("wlu_btn_deny", "Deny")

                ticketChannel.sendMessageEmbeds(embed.build())
                    .addActionRow(btnApprove, btnDeny)
                    .queue()

                val pingMention = if (modRole != null) modRole.asMention else "@here"
                ticketChannel.sendMessage("Application created for ${user.asMention} | $pingMention").queue()

                event.reply("✅ Created at: ${ticketChannel.asMention}").setEphemeral(true).queue()

            }, { error ->
                event.reply("❌ Error: Failed to create ticket channel. Reason: ${error.message}").setEphemeral(true).queue()
            })
        }
    }

    private fun approvePlayerInGame(nickname: String, discordId: String) {
        Add(nickname)
    }
}
