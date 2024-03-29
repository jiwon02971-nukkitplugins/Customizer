package iKguana.customizer.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.Event;
import cn.nukkit.event.entity.EntityInteractEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementStepSlider;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.Config;
import iKguana.artonline.SimpleDialog.SimpleDialog;
import iKguana.customizer.Customizer;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.CustomizerEvents;
import iKguana.customizer.CustomizerExecutor;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CustomizerEntity extends CustomizerBase {
    public CustomizerEntity() {
        setName("CustomizerEntity");

        new EntityManager(Customizer.getInstance().getDataFolder());

        CustomizerEvents.playerLoginEvent.add(this);
        CustomizerEvents.playerTeleportEvent.add(this);
        CustomizerEvents.dataPacketReceiveEvent.add(this);

        EntityManager.getIt().loadAllEntities();

        CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals(CT.getMessage(getName(), "label"))) {
            if (sender.isPlayer()) {
                FormWindowSimple window = new FormWindowSimple("커스텀 엔티티", "사용하고싶은 기능을 클릭해주세요.");
                window.addButton(new ElementButton("엔티티 추가"));
                window.addButton(new ElementButton("엔티티 편집"));
                window.addButton(new ElementButton("엔티티 편집모드 ON/OFF"));
                window.addButton(new ElementButton("엔티티 삭제"));
                SimpleDialog.sendDialog(this, "form_ent_menu", (Player) sender, window);
            } else
                sender.sendMessage(CT.getMessage("__Public", "NOT_IN_GAME"));
            return true;
        }
        return false;
    }

    public void form_ent_menu(PlayerFormRespondedEvent event, Object data) {
        String label = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();

        if (label.equals("엔티티 추가")) {
            FormWindowCustom window = new FormWindowCustom("ADD_ENT");
            window.addElement(new ElementInput("NAMETAG", "String"));
            window.addElement(new ElementInput("SKIN", "String"));
            window.addElement(new ElementInput("SCRIPT", "String"));
            window.addElement(new ElementSlider("PITCH", -180, 180, 1, 0));
            window.addElement(new ElementSlider("PITCH", -180, 180, 1, 0));

            SimpleDialog.sendDialog(this, "form_ent_add", event.getPlayer(), window);
        } else if (label.equals("엔티티 삭제")) {
            SimpleDialog.sendDialog(this, "form_ent_remove", event.getPlayer(), SimpleDialog.Type.FILTERING);
        }
    }

    public void form_ent_add(PlayerFormRespondedEvent event, Object data) {
        FormResponseCustom response = (FormResponseCustom) event.getResponse();
        String nametag = response.getInputResponse(0).trim();
        Position pos = event.getPlayer();
        String skin = response.getInputResponse(1).trim();
        String script = response.getInputResponse(2).trim();

        if (nametag.length() != 0) {
            EntityManager.getIt().registerEntity(nametag, pos, skin, script, (int) response.getSliderResponse(3), (int) response.getSliderResponse(4));

            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "저장되었습니다.");
        } else
            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "NAMETAG CANNOT BE BLANK. USE __.");
    }

    @Override
    public void event(Event e) {
        if (e instanceof PlayerLoginEvent) {
            PlayerLoginEvent event = (PlayerLoginEvent) e;
            EntityManager.getIt().spawnAll(event.getPlayer());
        } else if (e instanceof PlayerTeleportEvent) {
            PlayerTeleportEvent event = (PlayerTeleportEvent) e;
            if (event.getFrom().getLevel() != event.getTo().getLevel())
                EntityManager.getIt().despawnAll(event.getPlayer(), event.getFrom().getLevel().getName());
        } else if (e instanceof DataPacketReceiveEvent) {
            DataPacketReceiveEvent event = (DataPacketReceiveEvent) e;

            if (event.getPacket() instanceof InventoryTransactionPacket) {
                InventoryTransactionPacket pk = (InventoryTransactionPacket) event.getPacket();

                if (pk.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY) {
                    UseItemOnEntityData data = (UseItemOnEntityData) pk.transactionData;
                    CEnt ent = EntityManager.getIt().getEntity(event.getPlayer().getLevel().getName(), data.entityRuntimeId);
//                        if (event.getPlayer().isOp() && data.itemInHand.equals(editor, false)) {
////                            sendDialog(event.getPlayer(), dialogType.EDITBOT, data.entityRuntimeId);
////                        } else
                    event.getPlayer().sendMessage(data.entityRuntimeId + "");
                    if (ent instanceof CEnt)
                        ent.execute(this, event.getPlayer(), event, ent);
                }
            }
        }
    }
}

class EntityManager {
    private static EntityManager $instance;
    private File SKINS_DIR, CFG_PATH;
    private Config cfg;

    public EntityManager(File DIR_PATH) {
        $instance = this;

        SKINS_DIR = new File(DIR_PATH, "Skins");
        SKINS_DIR.mkdirs();

        CFG_PATH = new File(DIR_PATH, "CustmoizerEntity.yml");
        cfg = new Config(CFG_PATH, Config.YAML);

        loadAllEntities();
    }

    public Skin getSkin(String name) {
        Skin skin = new Skin();
        skin.setSkinId(name);
        skin.setGeometryName("geometry.humanoid.customizer");

        File file = new File(SKINS_DIR, name + ".zip");
        if (file.isFile())
            try {
                ZipFile zipFile = new ZipFile(file);

                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    InputStream stream = zipFile.getInputStream(entry);

                    if (entry.getName().endsWith("model.json")) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                        String geometry = "";
                        String line;
                        while ((line = reader.readLine()) != null)
                            geometry += line;
                        reader.close();

                        skin.setGeometryData(geometry);
                    } else if (entry.getName().endsWith("skin.png")) {
                        BufferedImage png = ImageIO.read(zipFile.getInputStream(entry));
                        skin.setSkinData(png);
                    }
                    stream.close();
                }

                return skin;
            } catch (Exception err) {
            }
        return new Skin();
    }

    public void loadAllEntities() {
        for (String level : cfg.getKeys(false)) {
            ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) cfg.get(level);
            for (HashMap<String, Object> m : list) {
                Position pos = new Position((int) m.get("x"), (int) m.get("y"), (int) m.get("z"), Server.getInstance().getLevelByName(level));
                CEnt bot = new CEnt((String) m.get("tag"), pos, (String) m.get("skin"), (String) m.get("script"), (int) m.get("pitch"), (int) m.get("yaw"));
                if (!entities.containsKey(level))
                    entities.put(level, new ArrayList<>());
                entities.get(level).add(bot);
                bot.spawnToAll();
            }
        }
    }

    public void registerEntity(String nametag, Position pos, String script, String skin, int pitch, int yaw) {
        CEnt ent = new CEnt(nametag, pos, script, skin, pitch, yaw);

        ArrayList<Map> list;
        if (cfg.exists(pos.getLevel().getName()))
            list = (ArrayList<Map>) cfg.getMapList(pos.getLevel().getName());
        else
            list = new ArrayList<>();

        list.add(ent.getData());

        cfg.set(pos.getLevel().getName(), list);
        cfg.save();

        ent.spawnToAll();
    }

    HashMap<String, ArrayList<CEnt>> entities = new HashMap<>();

    public CEnt getEntity(String level, long entId) {
        if (entities.containsKey(level))
            for (CEnt ent : entities.get(level))
                if (ent.id == entId)
                    return ent;
        return null;
    }


    public void spawnAll(Player player) {
        String level = player.getLevel().getName();
        if (entities.containsKey(level))
            for (CEnt ent : entities.get(level))
                ent.spawnTo(player);
    }

    public void despawnAll(Player player, String level) {
        if (entities.containsKey(level))
            for (CEnt ent : entities.get(level))
                ent.despawnTo(player);
    }

    public static EntityManager getIt() {
        return $instance;
    }
}

class CEnt {
    public UUID uuid;
    public String tag;
    public long id;
    public int x, y, z;
    public String level;
    public String skin;
    public String script;
    public int pitch, yaw;

    public CEnt(String tag, Position pos, String skin, String script, int pitch, int yaw) {
        this.uuid = UUID.randomUUID();
        this.tag = tag;
        this.x = pos.getFloorX();
        this.y = pos.getFloorY();
        this.z = pos.getFloorZ();
        this.level = pos.getLevel().getName();
        this.id = Entity.entityCount++;
        this.skin = skin;
        this.script = script;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public void execute(CustomizerBase cb, Player player, Event e, Object data) {
        CustomizerExecutor.executeScript(cb, player, CustomizerScript.getScript(script), e, data);
    }

    public void spawnTo(Player player) {
        if (!player.getLevel().getName().equals(level))
            return;

        AddPlayerPacket pk = new AddPlayerPacket();
        if (tag.startsWith("__"))
            pk.username = "";
        else
            pk.username = tag;
        pk.uuid = uuid;
        pk.entityRuntimeId = id;
        pk.entityUniqueId = id;
        pk.x = x + 0.5f;
        pk.y = y;
        pk.z = z + 0.5f;
        pk.pitch = pitch;
        pk.yaw = yaw;
        pk.item = Item.get(Item.AIR);
        player.batchDataPacket(pk);

        if (false) {//TODO FIX IT | It occurs crash in 1.13.
            PlayerListPacket pk_ = new PlayerListPacket();
            pk_.type = 0;
            pk_.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid, id, "", EntityManager.getIt().getSkin(skin), "")};
            player.batchDataPacket(pk_);

            pk_.type = 1;
            pk_.entries = new PlayerListPacket.Entry[]{new PlayerListPacket.Entry(uuid)};
            player.batchDataPacket(pk_);
        }
    }

    public void spawnToAll() {
        for (Player player : Server.getInstance().getOnlinePlayers().values())
            spawnTo(player);
    }

    public void despawnTo(Player player) {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        player.batchDataPacket(pk);
    }

    public void despawnToAll() {
        for (Player player : Server.getInstance().getOnlinePlayers().values())
            despawnTo(player);
    }

    public LinkedHashMap<String, Object> getData() {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("z", z);
        data.put("level", level);
        data.put("tag", tag);
        data.put("skin", skin);
        data.put("script", script);
        data.put("pitch", pitch);
        data.put("yaw", yaw);
        return data;
    }
}