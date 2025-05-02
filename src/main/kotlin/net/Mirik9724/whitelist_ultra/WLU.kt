package net.Mirik9724.whitelist_ultra

import net.Mirik9724.whitelist_ultra.commands.WLUCommand
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import net.Mirik9724.whitelist_ultra.detectPlatform.initializePlatform
import org.yaml.snakeyaml.Yaml


class WLU : JavaPlugin() {

    companion object {
        lateinit var instance: WLU
        private const val NewestVersionOfPlugin = "https://raw.githubusercontent.com/Mirik9724/whitelist-ultra/main/V.txt"
        var nick: String = "Test"
        const val conf = "config.yml"
        const val old_wl_f = "data.json"
        const val whitelist_f = "whitelist.json"
    }

    private lateinit var translationsFile: File
    private lateinit var translationsConfig: FileConfiguration
    var versionOfPlugin: String = this.description.version

    override fun onEnable() {
        instance = this
        val pluginDir = this.dataFolder.absolutePath
        val file = File(pluginDir, whitelist_f)

        File(dataFolder, old_wl_f)
            .takeIf { it.exists() && !File(dataFolder, whitelist_f).exists() }
            ?.let { oldFile ->
                val newFile = File(dataFolder, whitelist_f)
                val renamed = oldFile.renameTo(newFile)
                if (renamed) {
                    logger.info("Old file $old_wl_f successfully renamed to $whitelist_f")
                    if (oldFile.exists()) {
                        if (oldFile.delete()) {
                            logger.info("Old file $old_wl_f deleted after rename.")
                        } else {
                            logger.warning("Failed to delete old file $old_wl_f after rename.")
                        }
                    }
                } else {
                    logger.warning("Failed to rename $old_wl_f to $whitelist_f")
                }
            } ?:
//            logger.info("No need to rename, either the old file doesn't exist or the new file already exists.")
        // Создание директорий и файла, если они не существуют
        file.parentFile?.mkdirs()
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    logger.info("File created successfully: ${file.absolutePath}")
                } else {
                    logger.info("The file already exists.")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val commandExecutor = WLUCommand(this)
        getCommand("whitelist-ultra")?.setExecutor(commandExecutor)

        loadTranslations()
        checkForUpdates(Bukkit.getConsoleSender())

        initializePlatform(this)

        logger.info("WLU has loaded")
    }

    fun loadTranslations() {
        translationsFile = File(dataFolder, conf)

        // Если файл отсутствует, копируем из ресурсов
        if (!translationsFile.exists()) {
            logger.info("File $conf is not found. Trying to create from resources...")
            saveResource(conf, false)
            logger.info("File $conf is successfully created.")
        }

        translationsConfig = YamlConfiguration.loadConfiguration(translationsFile)

        // Загружаем эталонный файл из ресурсов
        val defaultStream = getResource(conf)
        if (defaultStream != null) {
            val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))

            // Добавляем только отсутствующие ключи
            var updated = false
            for (key in defaultConfig.getKeys(true)) {
                if (!translationsConfig.contains(key)) {
                    translationsConfig.set(key, defaultConfig.getString(key))
                    updated = true
                }
            }

            // Если файл был обновлен, сохраняем изменения
            if (updated) {
                try {
                    translationsConfig.save(translationsFile)
                    logger.info("Translations file was updated with new entries.")
                } catch (e: IOException) {
                    logger.severe("Failed to save updated translations file: ${e.message}")
                }
            } else {
//                logger.info("Translations file is already up-to-date.")
            }
        } else {
            logger.severe("Default translations file ($conf) is missing in plugin resources.")
        }
    }

    // Метод для получения перевода
    fun gT(path: String, vararg args: Any): String {
        val raw = translationsConfig.getString(path) ?: return "Translation not found: $path"
        var message = raw

        // Если есть аргументы — применим форматирование
        if (args.isNotEmpty()) {
            message = String.format(message, *args)
        }

        // Заменяем @nick, @test
        message = message.replace("@nick", WLU.nick)

        // Заменяем плейсхолдеры из конфигурации
//        val placeholders = loadPlaceholders(dataFolder.toString() + conf)
        val placeholders = loadPlaceholders(File(dataFolder, conf).path)
        for ((placeholder, replacement) in placeholders) {
            message = message.replace(placeholder, replacement)
        }

        // Применяем цветовые коды
        val result = ChatColor.translateAlternateColorCodes('&', message)

        // Преобразуем в компонент
        val component: Component = MiniMessage.miniMessage().deserialize(result)

        // Возвращаем как строку
        return LegacyComponentSerializer.legacySection().serialize(component)
    }

    // Функция для загрузки плейсхолдеров из YAML файла
    fun loadPlaceholders(configPath: String): Map<String, String> {
        val yaml = Yaml()
        val file = File(configPath)
        val placeholders = mutableMapOf<String, String>()

        if (file.exists()) {
            val data = yaml.load<Map<String, Any>>(file.reader())
            val nested = data["placeholders"] as? Map<*, *>
            if (nested != null) {
                for ((key, value) in nested) {
                    if (key is String && value is String) {
                        placeholders[key] = value
                    }
                }
            } else {
                logger.warning("Placeholders section not found or invalid in $configPath")
            }
        } else {
            logger.warning("Placeholder file not found at $configPath")
        }

        return placeholders
    }



    fun checkForUpdates(sender: CommandSender) {
        try {
            // Получаем версию плагина с сайта
            val url = URL(NewestVersionOfPlugin)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var inputLine: String?
            while (reader.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            reader.close()

            val latestVersion = response.toString().trim()

            // Убираем буквы и сравниваем только числа
            val latestNumeric = latestVersion.replace("[^0-9.]".toRegex(), "")
            val pluginNumeric = versionOfPlugin.replace("[^0-9.]".toRegex(), "")

            val latestLetter = latestVersion.replace("[^a-zA-Z]".toRegex(), "")
            val pluginLetter = versionOfPlugin.replace("[^a-zA-Z]".toRegex(), "")

            // Разбиваем версии по точкам и сравниваем числа
            val latestParts = latestNumeric.split(".")
            val pluginParts = pluginNumeric.split(".")
            val maxLength = maxOf(latestParts.size, pluginParts.size)

            for (i in 0 until maxLength) {
                val num1 = if (i < latestParts.size) latestParts[i].toInt() else 0
                val num2 = if (i < pluginParts.size) pluginParts[i].toInt() else 0

                if (num1 > num2) {
                    logger.info("${gT("version.v_found")}${"https://modrinth.com/plugin/whitelist-ultra/version/$latestVersion"}")
                    return
                } else if (num1 < num2) {
                    logger.info(gT("version.v_no_found"))
                    return
                }
            }

            // Если числа совпадают, сравниваем буквы (b > a)
            if (latestLetter.compareTo(pluginLetter) > 0) {
                logger.info("${gT("version.v_found")}${"https://modrinth.com/plugin/whitelist-ultra/version/$latestVersion"}")
            } else {
                logger.info(gT("version.v_no_found"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDisable() {
        logger.info("${ChatColor.GRAY}Whitelist Ultra has unloaded")
    }
}


