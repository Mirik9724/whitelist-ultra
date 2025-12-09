package net.Mirik9724.whitelist_ultra

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.FileConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URL
import java.io.File
import net.Mirik9724.api.*
import org.yaml.snakeyaml.Yaml

object WLUCore{
    var latestVersion: String = ""
    public val logger: Logger = LoggerFactory.getLogger("WhiteList Ultra")
    private const val NewestVersionOfPlugin = "https://raw.githubusercontent.com/Mirik9724/whitelist-ultra/main/V.txt"
    var nick: String = "Test"
    const val conf = "config.yml"
    const val old_wl_f = "data.json"
    const val whitelist_f = "whitelist.json"
    var dataFolder = File("plugins/whitelist_ultra")
    val dirWL = File(dataFolder, whitelist_f)

    private lateinit var translationsFile: File
    private lateinit var translationsConfig: FileConfiguration
    var translationsConfigMap: MutableMap<String, Any> = mutableMapOf()
    var versionOfPlugin: String = vers

    lateinit var file: File


    init {

        LogInit("WhiteList-Ultra")

        val configFile = Config.cloneConfigFromJar(conf, File("plugins/whitelist_ultra"))
        val configData = Config.loadYamlConfig(configFile)

        File(dataFolder, old_wl_f)
            .takeIf { it.exists() && !File(dataFolder, whitelist_f).exists() }
            ?.let { oldFile ->
                val newFile = File(dataFolder, whitelist_f)
                val renamed = oldFile.renameTo(newFile)
                if (renamed) {
                    logger.info("Old file ${old_wl_f} successfully renamed to ${whitelist_f}")
                    if (oldFile.exists()) {
                        if (oldFile.delete()) {
                            logger.info("Old file ${old_wl_f} deleted after rename.")
                        } else {
                            logger.warn("Failed to delete old file ${old_wl_f} after rename.")
                        }
                    }
                } else {
                    logger.warn("Failed to rename ${old_wl_f} to ${whitelist_f}")
                }
            }

        val dirWL = File(dataFolder, whitelist_f)
        if (!dirWL.parentFile.exists()) {
            dirWL.parentFile.mkdirs()
        }
        if (!dirWL.exists()) {
            dirWL.createNewFile()
        }

        loadTranslations()
        if(gT("check_updates").toString() == "True"){
            checkForUpdates()
        }

    }

    fun checkForUpdates() {
        try {
            val url = URL(NewestVersionOfPlugin)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            latestVersion = response.trim()

            // Оставляем только цифры и точки
            val latestNumeric = latestVersion.replace("[^0-9.]".toRegex(), "")
            val pluginNumeric = versionOfPlugin.replace("[^0-9.]".toRegex(), "")

            // Оставляем только буквы
            val latestLetter = latestVersion.replace("[^a-zA-Z]".toRegex(), "")
            val pluginLetter = versionOfPlugin.replace("[^a-zA-Z]".toRegex(), "")

            val latestParts = latestNumeric.split(".")
            val pluginParts = pluginNumeric.split(".")
            val maxLength = maxOf(latestParts.size, pluginParts.size)

            for (i in 0 until maxLength) {
                val num1 = if (i < latestParts.size) latestParts[i].toIntOrNull() ?: 0 else 0
                val num2 = if (i < pluginParts.size) pluginParts[i].toIntOrNull() ?: 0 else 0

                if (num1 > num2) {
                    logger.info("${gT("version.v_found")} https://www.curseforge.com/minecraft/bukkit-plugins/whitelist-ultra/file")
                    return
                } else if (num1 < num2) {
                    logger.info(gT("version.v_no_found").toString())
                    return
                }
            }

            // Если цифровые части совпадают, сравниваем буквы (b > a)
            if (latestLetter.compareTo(pluginLetter) > 0) {
                logger.info("${gT("version.v_found")} https://www.curseforge.com/minecraft/bukkit-plugins/whitelist-ultra/file")
            } else {
                logger.info(gT("version.v_no_found").toString())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadTranslations() {
        val targetDir = dataFolder
        val resourcePath = conf

        // Копируем и обновляем файл из ресурсов
        val configFile = Config.cloneConfigFromJar(resourcePath, targetDir)

        // Загружаем конфигурацию как Map<String, Any>
        val loaded = Config.loadYamlConfig(configFile)
        translationsConfigMap = if (loaded is Map<*, *>) {
            loaded.toMutableMap() as MutableMap<String, Any>
        } else {
            mutableMapOf()
        }

        // Загружаем дефолтный конфиг из ресурсов тоже как Map
        val defaultStream = javaClass.classLoader.getResourceAsStream(resourcePath)
        if (defaultStream == null) {
            logger.error("Default translations file ($resourcePath) is missing in plugin resources.")
            return
        }
        val defaultConfig = Config.yaml.load<Map<String, Any>>(defaultStream) ?: emptyMap()

        // Функция для слияния недостающих ключей
        fun mergeDefaults(defaults: Map<String, Any>, current: MutableMap<String, Any>): Boolean {
            var updated = false
            for ((key, value) in defaults) {
                if (value is Map<*, *> && current[key] is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    val nestedUpdated = mergeDefaults(
                        value as Map<String, Any>,
                        current[key] as MutableMap<String, Any>
                    )
                    if (nestedUpdated) updated = true
                } else if (!current.containsKey(key)) {
                    current[key] = value
                    updated = true
                }
            }
            return updated
        }

        val updated = mergeDefaults(defaultConfig, translationsConfigMap)

        if (updated) {
            Config.saveYamlToFile(configFile, translationsConfigMap)
            logger.info("Translations file was updated with new entries.")
        }
    }

    // Метод для получения перевода
    fun gT(path: String, vararg args: Any): String {
        val keys = path.split(".")
        var current: Any? = translationsConfigMap
        for (key in keys) {
            if (current is Map<*, *>) {
                current = current[key]
            } else {
                return "Translation not found: $path"
            }
        }

        val raw = current?.toString() ?: return "Translation not found: $path"
        var message = raw

        // Если есть аргументы — применим форматирование
        if (args.isNotEmpty()) {
            message = String.format(message, *args)
        }

        // Заменяем @nick, @test
        message = message.replace("@nick", nick)
        message = message.replace("@new_v", latestVersion)

        // Заменяем плейсхолдеры из конфигурации
//        val placeholders = loadPlaceholders(dataFolder.toString() + conf)
        val placeholders = loadPlaceholders(File(dataFolder, conf).path)
        for ((placeholder, replacement) in placeholders) {
            message = message.replace(placeholder, replacement)
        }

        // Применяем цветовые коды
        val result = translateAlternateColorCodes('&', message)

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
                logger.warn("Placeholders section not found or invalid in $configPath")
            }
        } else {
            logger.warn("Placeholder file not found at $configPath")
        }

        return placeholders
    }


    fun translateAlternateColorCodes(altColorChar: Char, textToTranslate: String): String {
        val chars = textToTranslate.toCharArray()
        for (i in 0 until chars.size - 1) {
            if (chars[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
                chars[i] = '§'
                chars[i + 1] = chars[i + 1].lowercaseChar()
            }
        }
        return String(chars)
    }
}
