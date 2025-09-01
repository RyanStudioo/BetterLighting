package io.github.ryanstudioo.betterLighting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Torch implements Listener {
    private final BetterLighting plugin;
    private final Map<UUID, Set<Block>> playerLightBlocks = new HashMap<>();
    private final Map<UUID, Map<Block, Material>> originalBlocks = new HashMap<>();

    public Torch(BetterLighting plugin) {
        this.plugin = plugin;
    }

    private boolean isLightSource(ItemStack item) {
        Set<Material> materials = Set.of(Material.TORCH, Material.LANTERN, Material.SOUL_TORCH, Material.SOUL_LANTERN);
        return item != null && materials.contains(item.getType());
    }

    private void isHoldingLightSource(Player player) {
        removePlayerLights(player.getUniqueId());
        ItemStack mainItem = player.getInventory().getItemInMainHand();
        ItemStack offItem = player.getInventory().getItemInOffHand();
        Location position = player.getLocation().add(0,1,0);
        Block block = position.getBlock();
        if ((isLightSource(mainItem) || isLightSource(offItem)) && block.isPassable()) {
            placeLights(block, player.getUniqueId());
        }
        if ((isLightSource(mainItem) || isLightSource(offItem))) {
            block.isPassable();
        }
    }

    private void placeLights(Block block, UUID playerId){
        Map<Block, Material> originalBlockTypes = new HashMap<>();
        originalBlockTypes.put(block, block.getType());

        block.setType(Material.LIGHT);

        Set<Block> lightBlocks = playerLightBlocks.computeIfAbsent(playerId, k -> new HashSet<>());
        lightBlocks.add(block);
        originalBlocks.put(playerId, originalBlockTypes);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayerLights(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        isHoldingLightSource(player);
        }


    @EventHandler
    public void OnPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            isHoldingLightSource(player);
        }, 1L);
    }

    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        isHoldingLightSource(player);
    }

    private void removePlayerLights(UUID playerId) {
        Set<Block> lightBlocks = playerLightBlocks.get(playerId);
        if (lightBlocks != null) {
            Map<Block, Material> originalBlockTypes = originalBlocks.get(playerId);
            for (Block block : lightBlocks) {

                Material originalType = originalBlockTypes.get(block);
                if (block.getType() == Material.LIGHT) {
                    if (originalType != null) {
                        block.setType(originalType);
                    } else {
                        block.setType(Material.AIR);
                    }
                }
            }
            playerLightBlocks.remove(playerId);
            originalBlocks.remove(playerId);
        }
    }
}