//package net.Mirik9724.whitelist_ultra
//
//import org.bstats.MetricsBase
//import org.bstats.charts.SimplePie
//import org.bstats.charts.SingleLineChart
//import java.util.*
//import java.util.concurrent.Callable
//
//class Metric {
//
//    fun initMetrics(
//        pluginName: String,
//        pluginVersion: String,
//        serviceId: Int,
//        whitelistData: Map<String, Any>,
//        configLanguage: String
//    ) {
//        val config = MetricsBase.Builder()
//            .pluginName(pluginName)
//            .pluginVersion(pluginVersion)
//            .customCharts(listOf(
//                SimplePie("language", Callable { configLanguage.lowercase(Locale.getDefault()) }),
//                SingleLineChart("whitelisted_users_count", Callable { whitelistData.size }),
//                SingleLineChart("placeholder_count", Callable { 0 }) // замените на свою логику
//            ))
//            .serviceId(serviceId)
//            .platform("unknown") // Можно указать "standalone" или любое название
//            .build()
//
//        MetricsBase.start(config)
//    }
//}
