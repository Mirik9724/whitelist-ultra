package net.Mirik9724.whitelist_ultra.Ds

import net.Mirik9724.whitelist_ultra.Commands.Add
import net.Mirik9724.whitelist_ultra.WLUCore.dsdata
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import java.awt.Color
import java.util.EnumSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class DSListener : ListenerAdapter() {

    private val deletionTasks = ConcurrentHashMap<String, ScheduledFuture<*>>()

    private fun getActiveQuestions(): List<Pair<String, String>> {
        val questions = mutableListOf<Pair<String, String>>()

        for (i in 1..5) {
            val question = dsdata["modal.q$i"]
            if (question.isNullOrBlank()) continue

            val placeholder = dsdata["modal.a$i"]
                ?: "Type your answer..."

            questions.add(Pair(question, placeholder))
        }

        return questions
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        val embed = EmbedBuilder()
            .setTitle(dsdata["embedTitle"]!!)
            .setDescription(dsdata["embedDescription"]!!)
            .setColor(Color.decode(dsdata["embedColor"]!!))
            .build()

        val startButton = Button.primary(
            "wlu_btn_start_ticket",
            dsdata["buttonText"]!!
        )

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
                event.reply(dsdata["no_questions"]!!)
                    .setEphemeral(true)
                    .queue()
                return
            }

            val actionRows = mutableListOf<ActionRow>()

            activeQuestions.forEachIndexed { index, pair ->

                val question = pair.first
                val placeholder = pair.second

                val inputField = TextInput.create(
                    "f_$index",
                    question,
                    TextInputStyle.SHORT
                )
                    .setPlaceholder(placeholder)
                    .setRequired(true)
                    .build()

                actionRows.add(ActionRow.of(inputField))
            }

            val modal = Modal.create(
                "wlu_modal",
                dsdata["modal_title"]!!
            )
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
            event.reply(dsdata["no_permission"]!!)
                .setEphemeral(true)
                .queue()
            return
        }

        val topic = channel.topic ?: ""

        val targetUserId: String
        val targetNick: String

        if (topic.startsWith("WLU-Ticket:") && topic.contains("||")) {

            val raw = topic.substringAfter("WLU-Ticket:")

            targetUserId = raw.substringBefore("||").trim()
            targetNick = raw.substringAfter("||").trim()

        } else {

            event.reply(dsdata["invalid_ticket"]!!)
                .setEphemeral(true)
                .queue()
            return
        }

        when (event.componentId) {

            "wlu_btn_approve" -> {

                event.deferEdit().queue()

                guild.retrieveMemberById(targetUserId).queue({ member ->

                    if (!guild.selfMember.canInteract(member)) {

                        channel.sendMessage(
                            dsdata["cannot_manage_user"]!!
                        ).queue()

                        approvePlayerInGame(targetNick, targetUserId)

                        channel.sendMessage(
                            dsdata["game_db_updated"]!!
                                .replace("%nick%", targetNick)
                        ).queue()

                        return@queue
                    }

                    val wlRoleId =
                        dsdata["whitelistRoleId"]
                            ?: dsdata["moderatorRoleId"]

                    if (!wlRoleId.isNullOrBlank()) {

                        val role = guild.getRoleById(wlRoleId)

                        if (role != null) {

                            guild.addRoleToMember(member, role).queue({

                                channel.sendMessage(
                                    dsdata["role_added"]!!
                                        .replace("%role%", role.name)
                                        .replace("%user%", member.asMention)
                                ).queue()

                            }, { error ->

                                channel.sendMessage(
                                    dsdata["role_failed"]!!
                                        .replace("%error%", error.message ?: "Unknown")
                                ).queue()
                            })
                        }
                    }

                    member.modifyNickname(targetNick).queue({

                        channel.sendMessage(
                            dsdata["nick_changed"]!!
                                .replace("%nick%", targetNick)
                                .replace("%user%", member.asMention)
                        ).queue()

                    }, { error ->

                        channel.sendMessage(
                            dsdata["nick_failed"]!!
                                .replace("%error%", error.message ?: "Unknown")
                        ).queue()
                    })

                    approvePlayerInGame(targetNick, targetUserId)

                    channel.sendMessage(
                        dsdata["game_db_updated"]!!
                            .replace("%nick%", targetNick)
                    ).queue()

                }, {

                    channel.sendMessage(
                        dsdata["user_not_found"]!!
                    ).queue()
                })

                val approvedEmbed = EmbedBuilder(
                    event.message.embeds.firstOrNull() ?: return
                )
                    .setColor(Color.BLUE)
                    .setTitle(dsdata["application_approved"]!!)
                    .build()

                val btnRestore = Button.primary(
                    "wlu_btn_restore",
                    dsdata["restore_button"]!!
                )

                event.hook.editMessageEmbedsById(
                    event.messageId,
                    approvedEmbed
                )
                    .setComponents(ActionRow.of(btnRestore))
                    .queue()

                channel.sendMessage(
                    dsdata["approved_by"]!!
                        .replace("%user%", event.user.asMention)
                ).queue()

                val scheduler = Executors.newSingleThreadScheduledExecutor()

                val scheduledTask = scheduler.schedule({

                    channel.delete().queue(
                        { scheduler.shutdown() },
                        { scheduler.shutdown() }
                    )

                    deletionTasks.remove(channel.id)

                }, 1, TimeUnit.MINUTES)

                deletionTasks[channel.id] = scheduledTask
            }

            "wlu_btn_deny" -> {

                event.deferEdit().queue()

                val deniedEmbed = EmbedBuilder(
                    event.message.embeds.firstOrNull() ?: return
                )
                    .setColor(Color.RED)
                    .setTitle(dsdata["application_denied"]!!)
                    .build()

                val btnRestore = Button.primary(
                    "wlu_btn_restore",
                    dsdata["restore_button"]!!
                )

                event.hook.editMessageEmbedsById(
                    event.messageId,
                    deniedEmbed
                )
                    .setComponents(ActionRow.of(btnRestore))
                    .queue()

                channel.sendMessage(
                    dsdata["denied_by"]!!
                        .replace("%user%", event.user.asMention)
                ).queue()

                val scheduler = Executors.newSingleThreadScheduledExecutor()

                val scheduledTask = scheduler.schedule({

                    channel.delete().queue(
                        { scheduler.shutdown() },
                        { scheduler.shutdown() }
                    )

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

                    val activeEmbed = EmbedBuilder(
                        event.message.embeds.firstOrNull() ?: return
                    )
                        .setColor(Color.GREEN)
                        .setTitle(dsdata["application_active"]!!)
                        .build()

                    val btnApprove = Button.success(
                        "wlu_btn_approve",
                        dsdata["approve_button"]!!
                    )

                    val btnDeny = Button.danger(
                        "wlu_btn_deny",
                        dsdata["deny_button"]!!
                    )

                    event.hook.editMessageEmbedsById(
                        event.messageId,
                        activeEmbed
                    )
                        .setComponents(
                            ActionRow.of(btnApprove, btnDeny)
                        )
                        .queue()

                    channel.sendMessage(
                        dsdata["deletion_cancelled"]!!
                            .replace("%user%", event.user.asMention)
                    ).queue()

                } else {

                    channel.sendMessage(
                        dsdata["deletion_not_found"]!!
                    ).queue()
                }
            }
        }
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {

        if (event.modalId != "wlu_modal") return

        val user = event.user
        val guild = event.guild ?: return
        val memberCreator = event.member ?: return

        var channelNameTemplate =
            dsdata["namesOfTickets"] ?: "@q1-@q2"

        val activeQuestions = getActiveQuestions()

        val answersMap = mutableMapOf<String, String>()

        activeQuestions.forEachIndexed { index, _ ->

            val answer =
                event.getValue("f_$index")?.asString ?: "unknown"

            answersMap["q${index + 1}"] = answer
        }

        val playerNick =
            answersMap["q1"] ?: "unknown"

        for (i in 1..5) {

            val replacement =
                answersMap["q$i"] ?: ""

            channelNameTemplate =
                channelNameTemplate.replace(
                    "@q$i",
                    replacement
                )
        }

        val finalChannelName = channelNameTemplate
            .lowercase()
            .replace(Regex("[^a-z0-9\\-_]"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')

        val safeChannelName =
            if (finalChannelName.isBlank())
                "ticket-${user.name.lowercase()}"
            else
                finalChannelName

        val categoryId = dsdata["ticketCategoryId"]

        val category =
            if (!categoryId.isNullOrBlank())
                guild.getCategoryById(categoryId)
            else
                null

        val modRoleId = dsdata["moderatorRoleId"]

        val modRole =
            if (!modRoleId.isNullOrBlank())
                guild.getRoleById(modRoleId)
            else
                null

        var channelAction = guild
            .createTextChannel(safeChannelName, category)
            .setTopic("WLU-Ticket:${user.id}||$playerNick")
            .addPermissionOverride(
                guild.publicRole,
                null,
                EnumSet.of(Permission.VIEW_CHANNEL)
            )
            .addPermissionOverride(
                guild.selfMember,
                EnumSet.of(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_SEND
                ),
                null
            )
            .addPermissionOverride(
                memberCreator,
                EnumSet.of(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_SEND
                ),
                null
            )

        if (modRole != null) {

            channelAction = channelAction.addPermissionOverride(
                modRole,
                EnumSet.of(
                    Permission.VIEW_CHANNEL,
                    Permission.MESSAGE_SEND
                ),
                null
            )
        }

        channelAction.queue({ ticketChannel ->

            val embed = EmbedBuilder()
                .setTitle(dsdata["application_active"]!!)
                .setColor(Color.GREEN)
                .addField("User:", user.asMention, false)

            activeQuestions.forEachIndexed { index, pair ->

                val answer =
                    event.getValue("f_$index")?.asString
                        ?: "No answer"

                embed.addField(
                    pair.first,
                    answer,
                    false
                )
            }

            embed.setFooter("User ID: ${user.id}")

            val btnApprove = Button.success(
                "wlu_btn_approve",
                dsdata["approve_button"]!!
            )

            val btnDeny = Button.danger(
                "wlu_btn_deny",
                dsdata["deny_button"]!!
            )

            ticketChannel.sendMessageEmbeds(embed.build())
                .addActionRow(btnApprove, btnDeny)
                .queue()

            val pingMention =
                if (modRole != null)
                    modRole.asMention
                else
                    "@here"

            ticketChannel.sendMessage(
                dsdata["ticket_created"]!!
                    .replace("%user%", user.asMention)
                    .replace("%ping%", pingMention)
            ).queue()

            event.reply(
                dsdata["ticket_created_reply"]!!
                    .replace("%channel%", ticketChannel.asMention)
            )
                .setEphemeral(true)
                .queue()

        }, { error ->

            event.reply(
                dsdata["ticket_create_failed"]!!
                    .replace("%error%", error.message ?: "Unknown")
            )
                .setEphemeral(true)
                .queue()
        })
    }

    private fun approvePlayerInGame(
        nickname: String,
        discordId: String
    ) {
        Add(nickname)
    }
}