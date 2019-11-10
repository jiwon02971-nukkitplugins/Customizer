package iKguana.customizer;

import java.util.ArrayList;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInvalidMoveEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import iKguana.customizer.interfaces.CustomizerBase;

public class CustomizerEvents implements Listener {
    public static ArrayList<CustomizerBase> levelLoadEvent = new ArrayList<>();

    @EventHandler
    public void playerJoinEvent(LevelLoadEvent event) {
        for (CustomizerBase c : levelLoadEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> dataPacketReceiveEvent = new ArrayList<>();

    @EventHandler
    public void dataPacketReceiveEvent(DataPacketReceiveEvent event) {
        for (CustomizerBase c : dataPacketReceiveEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerJoinEvent = new ArrayList<>();

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        for (CustomizerBase c : playerJoinEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerMoveEvent = new ArrayList<>();

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent event) {
        for (CustomizerBase c : playerMoveEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerLoginEvent = new ArrayList<>();

    @EventHandler
    public void playerLoginEvent(PlayerLoginEvent event) {
        for (CustomizerBase c : playerLoginEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerQuitEvent = new ArrayList<>();

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        for (CustomizerBase c : playerQuitEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerChatEvent = new ArrayList<>();

    @EventHandler
    public void playerChatEvent(PlayerChatEvent event) {
        for (CustomizerBase c : playerChatEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerCommandPreprocessEvent = new ArrayList<>();

    @EventHandler
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        for (CustomizerBase c : playerCommandPreprocessEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerFormRespondedEvent = new ArrayList<>();

    @EventHandler
    public void playerFormRespondedEvent(PlayerFormRespondedEvent event) {
        for (CustomizerBase c : playerFormRespondedEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerFoodLevelChangeEvent = new ArrayList<>();

    @EventHandler
    public void playerFoodLevelChangeEvent(PlayerFoodLevelChangeEvent event) {
        for (CustomizerBase c : playerFoodLevelChangeEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> itemFrameDropItemEvent = new ArrayList<>();

    @EventHandler
    public void playerFoodLevelChangeEvent(ItemFrameDropItemEvent event) {
        for (CustomizerBase c : itemFrameDropItemEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerDeathEvent = new ArrayList<>();

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        for (CustomizerBase c : playerDeathEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerTeleportEvent = new ArrayList<>();

    @EventHandler
    public void playerTeleportEvent(PlayerTeleportEvent event) {
        for (CustomizerBase c : playerTeleportEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerDropItemEvent = new ArrayList<>();

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent event) {
        for (CustomizerBase c : playerDropItemEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerInteractEvent = new ArrayList<>();

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        for (CustomizerBase c : playerInteractEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> entityInteractEvent = new ArrayList<>();

    @EventHandler
    public void entityInteractEvent(EntityInteractEvent event) {
        for (CustomizerBase c : entityInteractEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> entityInventoryChangeEvent = new ArrayList<>();

    @EventHandler
    public void entityInventoryChangeEvent(EntityInventoryChangeEvent event) {
        for (CustomizerBase c : entityInventoryChangeEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> blockBreakEvent = new ArrayList<>();

    @EventHandler
    public void entityInventoryChangeEvent(BlockBreakEvent event) {
        for (CustomizerBase c : blockBreakEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> playerInvalidMoveEvent = new ArrayList<>();

    @EventHandler
    public void playerInvalidMoveEvent(PlayerInvalidMoveEvent event) {
        for (CustomizerBase c : playerInvalidMoveEvent)
            c.event(event);
    }

    public static ArrayList<CustomizerBase> pluginEnableEvent = new ArrayList<>();

    @EventHandler
    public void pluginEnableEvent(PluginEnableEvent event) {
        for (CustomizerBase c : pluginEnableEvent)
            c.event(event);
    }
}
