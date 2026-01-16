package net.Mirik9724.api

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

fun toMM(input: String): Component {
    return try {
        MiniMessage.miniMessage().deserialize(input)
    } catch (e: Throwable) {
        Component.text(input)
    }

}

