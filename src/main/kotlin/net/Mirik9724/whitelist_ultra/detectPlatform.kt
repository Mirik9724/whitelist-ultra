package net.Mirik9724.whitelist_ultra

import net.Mirik9724.whitelist_ultra.events.BungeePlayerLoginListener
import net.Mirik9724.whitelist_ultra.events.SpigotPlayerLoginListener
import net.Mirik9724.whitelist_ultra.events.VelocityPlayerLoginListener
import org.bukkit.plugin.java.JavaPlugin

enum class Platform {
    SPIGOT,
    BUNGEECORD,
    VELOCITY,
    UNKNOWN
}

object detectPlatform {
    var platf = ""

    val spigotPlugin = WLU.instance
    var bungeePlugin: net.md_5.bungee.api.plugin.Plugin? = null
    var velocityPlugin: com.velocitypowered.api.plugin.Plugin? = null

    // Логирование для разных платформ
    fun log(type: String, text: String): Unit {
        when (platf.lowercase()) {
            "bu" -> {
                val proxy = net.md_5.bungee.api.ProxyServer.getInstance()
                val color = when (type.lowercase()) {
                    "i" -> net.md_5.bungee.api.ChatColor.GRAY
                    "w" -> net.md_5.bungee.api.ChatColor.YELLOW
                    "f" -> net.md_5.bungee.api.ChatColor.RED
                    else -> net.md_5.bungee.api.ChatColor.GRAY
                }
                val prefix = when (type.lowercase()) {
                    "i" -> "[INFO]"
                    "w" -> "[WARNING]"
                    "f" -> "[FATAL]"
                    else -> "[INFO]"
                }
                proxy.console.sendMessage(net.md_5.bungee.api.chat.TextComponent("${color}$prefix $text"))
            }

            "ve" -> {
                val logger = WLU.instance.logger
                val colorPrefix = when (type.lowercase()) {
                    "i" -> "§7[INFO] "
                    "w" -> "§e[WARNING] "
                    "f" -> "§c[FATAL] "
                    else -> "§7[INFO] "
                }
                logger.info("$colorPrefix$text")
            }

            else -> {
                val color = when (type.lowercase()) {
                    "i" -> org.bukkit.ChatColor.GRAY
                    "w" -> org.bukkit.ChatColor.YELLOW
                    "f" -> org.bukkit.ChatColor.RED
                    else -> org.bukkit.ChatColor.GRAY
                }
                val prefix = when (type.lowercase()) {
                    "i" -> "[INFO]"
                    "w" -> "[WARNING]"
                    "f" -> "[FATAL]"
                    else -> "[INFO]"
                }
                org.bukkit.Bukkit.getConsoleSender().sendMessage("${color}$prefix $text")
            }
        }
    }

    // Функция для определения платформы
    fun detectPlatform(): Platform {
        return when {
            // Проверка на Spigot
            classExists("org.bukkit.Bukkit") -> Platform.SPIGOT

            // Проверка на BungeeCord
            classExists("net.md_5.bungee.api.plugin.Plugin") -> Platform.BUNGEECORD

            // Проверка на Velocity
            classExists("com.velocitypowered.api.plugin.Plugin") -> Platform.VELOCITY

            // Если не удалось точно определить, возвращаем UNKNOWN
            else -> Platform.UNKNOWN
        }
    }

    // Вспомогательная функция для проверки наличия класса
    private fun classExists(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    // Инициализация Spigot
    fun SpInit(plugin: JavaPlugin) {

        plugin.server.pluginManager.registerEvents(SpigotPlayerLoginListener(plugin), plugin)
        platf = "Sp"
    }

    fun BuInit(plugin: net.md_5.bungee.api.plugin.Plugin) {
        bungeePlugin = plugin
        val server = net.md_5.bungee.api.ProxyServer.getInstance() // Получаем экземпляр ProxyServer
        server.pluginManager.registerListener(plugin, BungeePlayerLoginListener(plugin))
        platf = "Bu"
    }

    // Инициализация Velocity
    fun VeInit(plugin: com.velocitypowered.api.plugin.Plugin) {
        velocityPlugin = plugin
        val server = WLU.instance.server as com.velocitypowered.api.proxy.ProxyServer // Приводим к типу ProxyServer
        val pluginContainer = server.pluginManager.fromInstance(plugin).orElseThrow {
            IllegalStateException("PluginContainer not found for plugin instance")
        }
        server.eventManager.register(pluginContainer, VelocityPlayerLoginListener(server, pluginContainer))
        platf = "Ve"
    }


    // Инициализация платформы
    fun initializePlatform(plugin: JavaPlugin) {
        when (val platform = detectPlatform()) {
//            Platform.SPIGOT -> {
//                logger.info("Detected platform: Spigot")
//                val plugin = spigotPlugin as JavaPlugin
//                SpInit(plugin)
//            }
            Platform.SPIGOT -> {
                WLU.instance.logger.info("Detected platform: Spigot")

                val plugin = plugin
                SpInit(WLU.instance)
            }
            Platform.BUNGEECORD -> {
                WLU.instance.logger.info("Detected platform: BungeeCord")
                val plugin = bungeePlugin as net.md_5.bungee.api.plugin.Plugin
                BuInit(plugin)
            }
            Platform.VELOCITY -> {
                WLU.instance.logger.info("Detected platform: Velocity")
                val plugin = velocityPlugin as com.velocitypowered.api.plugin.Plugin
                VeInit(plugin)
            }
            Platform.UNKNOWN -> {
                val a = WLU.instance.gT("platform-custom")
                if (a == "BUNGEECORD") {
                    val plugin = bungeePlugin as net.md_5.bungee.api.plugin.Plugin
                    BuInit(plugin)
                } else if (a == "VELOCITY") {
                    val plugin = velocityPlugin as com.velocitypowered.api.plugin.Plugin
                    VeInit(plugin)
                } else {
                    val plugin = spigotPlugin as JavaPlugin
                    SpInit(plugin)
                }
            }
        }
    }
}
