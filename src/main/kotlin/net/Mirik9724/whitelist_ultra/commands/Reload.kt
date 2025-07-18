package net.Mirik9724.whitelist_ultra.commands

import net.Mirik9724.whitelist_ultra.WLUCore

object ReloadCommand {

    fun reload(): String {
        // В будущем сюда можно добавить: обновление переводов, кеша, файлов и т.д.
        return WLUCore.gT("reload")
    }
}
