package net.Mirik9724.whitelist_ultra

import net.Mirik9724.api.*
import net.Mirik9724.whitelist_ultra.Commands.loadWL
import net.Mirik9724.whitelist_ultra.Commands.wld
import org.slf4j.Logger
import org.yaml.snakeyaml.Yaml
import java.io.File


object WLUCore{
    var nick: String = "Test"
    const val conf = "config.yml"
    const val whitelist_f = "whitelist.json"
    var dataFolder = File("plugins/whitelist_ultra")
    val dirWL = File(dataFolder, whitelist_f)
    lateinit var data: Map<String, String>
    lateinit var placeholders: Map<String, String>
    const val udhp = "You do not have permission for this command"
    lateinit var log: Logger

    init {
        log = logInit("WLU")

        tryCreatePath(dataFolder)
        copyFileFromJar(conf, "plugins/whitelist_ultra", this.javaClass.classLoader)
        try{
            updateYmlFromJar(conf, "plugins/whitelist_ultra/" + conf, this::class.java.classLoader)
        }catch(e:Exception){
            log.info(e.toString())
        }

        data = loadYmlFile("plugins/whitelist_ultra/"+ conf)

        val dirWL = File(dataFolder, whitelist_f)
        if (!dirWL.parentFile.exists()) {
            dirWL.parentFile.mkdirs()
        }
        if (!dirWL.exists()) {
            dirWL.createNewFile()
        }

        placeholders = loadPlaceholders("plugins/whitelist_ultra/" + conf)

        if(gT("check_updates").toString() == "True"){
            if(isAvailableNewVersion("https://raw.githubusercontent.com/Mirik9724/whitelist-ultra/main/V.txt", vers) == true){

            }
        }

        wld = loadWL()
    }


    private fun loadPlaceholders(configPath: String): Map<String, String> {
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
                log.warn("Placeholders section not found or invalid in $configPath")
            }
        } else {
            log.warn("Placeholder file not found at $configPath")
        }

        return placeholders
    }

    private fun colorize(text: String): String {
        return text.replace("&", "§")
    }
    private fun applyPlaceholders(text: String): String {
        var result = text
        for ((key, value) in placeholders) {
            result = result.replace("{$key}", value)
        }
        return result
    }

    fun gT(key: String): String {
        val raw = data[key] ?: return "Null"

        val withPlaceholders = applyPlaceholders(raw.replace("@nick", nick))
        return colorize(withPlaceholders)
    }
}
