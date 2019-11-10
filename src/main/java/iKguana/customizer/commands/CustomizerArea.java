package iKguana.customizer.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Event;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import iKguana.customizer.Customizer;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.CustomizerEvents;
import iKguana.customizer.CustomizerExecutor;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;
import iKguana.simpledialog.SimpleDialog;

public class CustomizerArea extends CustomizerBase {
    public CustomizerArea() {
        setName("CustomizerArea");

        new AreaManager(new File(Customizer.getInstance().getDataFolder() + File.separator + getName() + ".yml"));

        CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));

        CustomizerEvents.playerInteractEvent.add(this);
        CustomizerEvents.playerMoveEvent.add(this);

        CustomizerExecutor.customGlobalPlaceHolders_.put("aareas", this);
        CustomizerExecutor.customGlobalPlaceHolders_.put("isin", this);
    }

    @Override
    public String getCustomPlaceHolder(String tag, Object data) {
        switch (tag) {
            case "enter":
                return String.valueOf(((Object[]) data)[0]);
            case "area":
                return (String) ((Object[]) data)[1];
        }
        return super.getCustomPlaceHolder(tag, data);
    }

    @Override
    public String getCustomGlobalPlaceHolder(String tag, String arg, Object data) {
        switch (tag) {
            case "aareas":
                break;

            case "isin":
               break;
        }
        return super.getCustomGlobalPlaceHolder(tag, arg, data);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals(CT.getMessage(getName(), "label"))) {
            if (sender.isPlayer()) {
                FormWindowSimple window = new FormWindowSimple("커스텀 영역", "사용할 기능을 클릭해주세요.");
                window.addButton(new ElementButton("영역 추가"));
                window.addButton(new ElementButton("영역 편집"));
                window.addButton(new ElementButton("영역 삭제"));
                SimpleDialog.sendDialog(this, "form_area_menu", (Player) sender, window);
            } else
                sender.sendMessage(CT.getMessage("__Public", "NOT_IN_GAME"));
            return true;
        }
        return false;
    }

    public void form_area_menu(PlayerFormRespondedEvent event, Object data) {
        String text = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        if (text.equals("영역 추가")) {
            FormWindowCustom window = new FormWindowCustom("영역 추가");
            window.addElement(new ElementInput("영역의 이름을 입력해주세요."));
            window.addElement(new ElementInput("사용할 스크립트를 입력해주세요."));
            window.addElement(new ElementToggle("y값 체크", false));

            SimpleDialog.sendDialog(this, "form_add_area", event.getPlayer(), window);
        } else if (text.equals("영역 편집")) {
            SimpleDialog.sendDialog(this, "form_edit_select_world", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(AreaManager.getIt().getLevels()));
        } else if (text.equals("영역 삭제")) {
            SimpleDialog.sendDialog(this, "form_delete_select_world", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(AreaManager.getIt().getLevels()));
        }
    }

    public void form_add_area(PlayerFormRespondedEvent event, Object data) {
        FormResponseCustom response = (FormResponseCustom) event.getResponse();

        String name = response.getInputResponse(0);
        String script = response.getInputResponse(1);
        if (name.trim().length() == 0 || script.trim().length() == 0) {
            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "공백은 사용할수 없습니다.");
            return;
        }
        boolean checkHeight = response.getToggleResponse(2);

        Area area = new Area();
        area.setName(name);
        area.setScript(script);
        area.setCheckHeight(checkHeight);
        queue.put(event.getPlayer(),area);

        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "첫번째와 두번째 지점을 지정해주세요.");
    }

    HashMap<String, String> selectedLevel = new HashMap<>();

    public void form_edit_select_world(PlayerFormRespondedEvent event, Object data) {
        String level = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        selectedLevel.put(event.getPlayer().getName(), level);

        SimpleDialog.sendDialog(this, "form_edit_select_area", event.getPlayer(), SimpleDialog.Type.FILTERING, AreaManager.getIt().getAll(level));
    }

    public void form_edit_select_area(PlayerFormRespondedEvent event, Object data) {
        String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        Area area = AreaManager.getIt().getArea(selectedLevel.get(event.getPlayer().getName()), name);

        FormWindowCustom window = new FormWindowCustom("구역 수정");
        window.addElement(new ElementLabel("구역을 편집합니다."));
        window.addElement(new ElementInput("스크립트", "입력해주세요.", area.getScript()));
        window.addElement(new ElementLabel("월드 : " + area.getLevel()));
        window.addElement(new ElementLabel("1좌표 : " + area.getFstPos().getFloorX()+", "+area.getFstPos().getFloorY()+", "+area.getFstPos().getFloorZ()));
        window.addElement(new ElementLabel("2좌표 : " + area.getSndPos().getFloorX()+", "+area.getSndPos().getFloorY()+", "+area.getSndPos().getFloorZ()));
        window.addElement(new ElementToggle("Y값 체크", area.getCheckHeight()));

        SimpleDialog.sendDialog(this, "form_edit_area", event.getPlayer(), window, area);
    }

    public void form_edit_area(PlayerFormRespondedEvent event, Object data) {
        String script = ((FormResponseCustom) event.getResponse()).getInputResponse(1);
        Boolean checkHeight = ((FormResponseCustom) event.getResponse()).getToggleResponse(5);

        Area area = (Area) data;
        area.setScript(script);
        area.setCheckHeight(checkHeight);
        AreaManager.getIt().editArea(area);

        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "수정되었습니다.");
    }

    public void form_delete_select_world(PlayerFormRespondedEvent event, Object data) {
        String level = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        selectedLevel.put(event.getPlayer().getName(), level);

        SimpleDialog.sendDialog(this, "form_delete_select_area", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(AreaManager.getIt().getAll(level)));
    }

    public void form_delete_select_area(PlayerFormRespondedEvent event, Object data) {
        String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        String world = selectedLevel.get(event.getPlayer().getName());

        AreaManager.getIt().removeArea(AreaManager.getIt().getArea(world,name));

        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "삭제되었습니다.");
    }

    // TODO REMOVE #1
    HashMap<String, Long> points = new HashMap<>();

    public boolean restrictCPS(PlayerInteractEvent event) {
        if (points.containsKey(event.getPlayer().getName())) {
            if (points.get(event.getPlayer().getName()) + 100 < (new Date()).getTime()) {
                points.replace(event.getPlayer().getName(), (new Date()).getTime());
            } else
                return true;
        } else
            points.put(event.getPlayer().getName(), (new Date()).getTime());
        return false;
    }

    // END
    HashMap<Player, Area> queue = new HashMap<>();

    @Override
    public void event(Event e) {
        if (e instanceof PlayerInteractEvent) {
            PlayerInteractEvent event = (PlayerInteractEvent) e;

            if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
                return;

            if (restrictCPS(event))// TODO REMOVE #1
                return;

            if (queue.containsKey(event.getPlayer())) {
                Area area = queue.get(event.getPlayer());
                if (area.getFstPos() == null) {
                    area.setLevel(event.getPlayer().getLevel().getName());
                    area.setFstPos(event.getBlock());

                    SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "첫번째 지점이 성공적으로 저장되었습니다.");
                } else if (area.getSndPos() == null) {
                    if (area.getLevel().equals(event.getBlock().getLevel().getName()))
                        area.setSndPos(event.getBlock());

                    if (area.isValid()) {
                        AreaManager.getIt().addArea(area);

                        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "성공적으로 저장되었습니다.");
                    } else
                        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "오류가 발생했습니다.");
                    queue.remove(event.getPlayer());
                }
            }
        } else if (e instanceof PlayerMoveEvent) {
            PlayerMoveEvent event = (PlayerMoveEvent) e;
            if (event.getFrom().getFloorX() != event.getTo().getFloorX() || event.getFrom().getFloorY() != event.getTo().getFloorY() || event.getFrom().getFloorZ() != event.getTo().getFloorZ()) {
                Position from = event.getFrom().clone();
                from.setComponents(from.x, from.y - 1, from.z);

                ArrayList<Area> fromAreas = AreaManager.getIt().getAreas(from);

                Position to = event.getTo().clone();
                to.setComponents(to.x, to.y - 1, to.z);

                ArrayList<Area> toAreas = AreaManager.getIt().getAreas(to);

                ArrayList<Area> in = new ArrayList<>();
                ArrayList<Area> out = new ArrayList<>();

                for (Area farea : fromAreas)
                    if (!toAreas.contains(farea))
                        out.add(farea);
                for (Area toarea : toAreas)
                    if (!fromAreas.contains(toarea))
                        in.add(toarea);

                for (Area i : in)
                    CustomizerExecutor.executeScript(this, event.getPlayer(), CustomizerScript.getScript(i.getScript()), event, new Object[]{true, i});
                for (Area o : out)
                    CustomizerExecutor.executeScript(this, event.getPlayer(), CustomizerScript.getScript(o.getScript()), event, new Object[]{false, o});
            }
        }
    }
}

class AreaManager {
    private static AreaManager instance;
    File CFG_PATH;
    Config cfg;

    public AreaManager(File CFG_PATH) {
        instance = this;

        this.CFG_PATH = CFG_PATH;
        cfg = new Config(CFG_PATH, Config.YAML);
    }

    HashMap<String,HashMap<String,Area>> areas = new HashMap();
    public void loadAll(){
        for (String level : getLevels()) {
            ConfigSection rawAreas = cfg.getSection(level);
            for (String name : getAll(level)) {
                ConfigSection rawArea = cfg.getSection(name);

                Area area = new Area(rawArea);
                if (!areas.containsKey(level))
                    areas.put(level, new HashMap<String, Area>());
                areas.get(level).put(area.getName(), area);
            }
        }
    }

    public void addArea( Area area){
        cfg.set(area.getLevel() + "." + area.getName(), area.getData());
        cfg.save();

        if (!areas.containsKey(area.getLevel()))
            areas.put(area.getLevel(), new HashMap<String, Area>());
        areas.get(area.getLevel()).put(area.getName(), area);
    }
    public void editArea(Area area){
        addArea(area);
    }
    public boolean isArea(Level level,String name){
        if (areas.containsKey(level) && areas.get(level).containsKey(name))
            return true;
        return false;
    }
    public Area getArea(String level, String name){
        return areas.get(level).get(name);
    }
    public ArrayList<Area> getAreas(Position pos){
        String level = pos.getLevel().getName();
        if (!areas.containsKey(level))
            return new ArrayList<>();

        ArrayList<Area> wholeAreas = new ArrayList<>(areas.get(level).values());

        ArrayList<Area> list = new ArrayList<>();

        for (Area area : wholeAreas)
            if (area.isIn(pos))
                list.add(area);

        return list;
    }
    public void removeArea(Area area){
        if (areas.containsKey(area.getLevel())) {
            areas.get(area.getLevel()).remove(area.getName());
        }

        cfg.getSection(area.getLevel()).remove(area.getName());
        if (areas.get(area.getLevel()).size() == 0) {
            areas.remove(area.getLevel());
            cfg.remove(area.getLevel());
        }
        cfg.save();
    }

    public ArrayList<String> getLevels(){ return new ArrayList<String>(cfg.getKeys(false)); }
    public ArrayList<String> getAll(String lvl){ return new ArrayList<String>(cfg.getSection(lvl).getKeys(false)); }

    public void reload(){ areas = new HashMap<>(); loadAll(); }

    public static AreaManager getIt(){return instance;}
}

class Area {
    private String name = null;
    private String script = null;
    private String level = null;
    private Vector3 pos1 = null;
    private Vector3 pos2 = null;
    private boolean checkHeight = true;

    public Area(){
    }
    public Area(ConfigSection datas){
        setName(datas.getString("Name"));
        setScript(datas.getString("Script"));
        setLevel(datas.getString("Level"));
        setFstPos(new Vector3(datas.getInt("fst.x"),datas.getInt("fst.y"),datas.getInt("fst.z")));
        setSndPos(new Vector3(datas.getInt("snd.x"),datas.getInt("snd.y"),datas.getInt("snd.z")));
        setCheckHeight(datas.getBoolean("CheckHeight"));
    }

    public void setName(String name){this.name=name;}
    public String getName(){return name;}
    public void setScript(String script){this.script=script;}
    public String getScript(){return script;}
    public void setLevel(String level){this.level=level;}
    public String getLevel(){return level;}
    public void setFstPos(Vector3 pos){this.pos1=pos;}
    public Vector3 getFstPos(){return pos1;}
    public void setSndPos(Vector3 pos){this.pos2=pos;}
    public Vector3 getSndPos(){ return pos2; }
    public void setCheckHeight(boolean bool){this.checkHeight=bool;}
    public boolean getCheckHeight(){return checkHeight;}

    public boolean isIn(Position pos) {
        if (isValid())
            if (level.equals(pos.getLevel().getName()))
                if (isMedium(pos1.getFloorX(), pos.getFloorX(), pos2.getFloorX()))
                    if (!checkHeight || isMedium(pos1.getFloorY(), pos.getFloorY(), pos2.getFloorY()))
                        if (isMedium(pos1.getFloorZ(), pos.getFloorZ(), pos2.getFloorZ()))
                            return true;
        return false;
    }
    private boolean isMedium(int num1,int var,int num2){
        int max = Math.max(num1, num2);
        int min = Math.min(num1, num2);
        if (min <= var && var <= max)
            return true;
        else
            return false;
    }

    public boolean isValid(){
        if (name != null && script != null && level != null && pos1 != null && pos2 != null)
            return true;
        return false;
    }

    public ConfigSection getData() {
        ConfigSection section = new ConfigSection();

        section.set("Name", getName());
        section.set("Script", getScript());
        section.set("Level", getLevel());
        section.set("fst.x", getFstPos().getFloorX());
        section.set("fst.y", getFstPos().getFloorY());
        section.set("fst.z", getFstPos().getFloorZ());
        section.set("snd.x", getSndPos().getFloorX());
        section.set("snd.y", getSndPos().getFloorY());
        section.set("snd.z", getSndPos().getFloorZ());
        section.set("CheckHeight", getCheckHeight());

        return section;
    }
}
