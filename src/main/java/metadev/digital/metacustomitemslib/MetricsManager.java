package metadev.digital.metacustomitemslib;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bukkit.Bukkit;

import metadev.digital.metacustomitemslib.HttpTools.httpCallback;


public class MetricsManager {

    private Core plugin;
    private boolean started = false;

    private Metrics bStatsMetrics;

    public MetricsManager(Core plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            public void run() {
                try {
                    // make a URL to MCStats.org
                    URL url = new URL("https://bstats.org/");
                    if (!started) {
                        plugin.getMessages().debug("check if home page can be reached");
                        HttpTools.isHomePageReachable(url, new httpCallback() {

                            @Override
                            public void onSuccess() {
                                startBStatsMetrics();
                                plugin.getMessages().debug("Metrics reporting to Https://bstats.org has started.");
                                started = true;
                            }

                            @Override
                            public void onError() {
                                started=false;
                                plugin.getMessages().debug("https://bstats.org/ seems to be down");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, 100L, 72000L);
    }

    public void startBStatsMetrics() {
        // https://bstats.org/what-is-my-plugin-id
        bStatsMetrics = new Metrics(plugin,22716);

        bStatsMetrics.addCustomChart(new DrilldownPie("economy_api", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            String economyAPI = plugin.getEconomyManager().getVersion();
            Map<String, Integer> entry = new HashMap<>();
            entry.put(economyAPI, 1);
            if (plugin.getEconomyManager().getVersion().endsWith("Vault")) {
                map.put("Vault", entry);
            } else if (plugin.getEconomyManager().getVersion().endsWith("Reserve")) {
                map.put("Reserve", entry);
            } else {
                map.put("None", entry);
            }
            return map;
        }));

    }

}
