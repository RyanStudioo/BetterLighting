package io.github.ryanstudioo.betterLighting;

import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public final class BetterLighting extends JavaPlugin implements Listener {


    @Override
    public void onEnable() {
        // Register the Torch listener
        getServer().getPluginManager().registerEvents(new Torch(this), this);

        getLogger().info("BetterLighting plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
