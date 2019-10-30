package iKguana.customizer.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Event;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import iKguana.customizer.Customizer;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.CustomizerEvents;
import iKguana.customizer.CustomizerExecutor;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;
import iKguana.simpledialog.SimpleDialog;

public class CustomizerClick extends CustomizerBase {
	Config areas;

	public CustomizerClick() {
		setName("CustomizerClick");

		areas = new Config(Customizer.getInstance().getDataFolder() + File.separator + getName() + ".yml");

		CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));

		CustomizerEvents.playerInteractEvent.add(this);

		CustomizerExecutor.customGlobalPlaceHolders_.put("careas", this);
	}

	@Override
	public String getCustomPlaceHolder(String tag, Object data) {
		switch (tag) {
		case "area":
			return (String) data;
		}
		return super.getCustomPlaceHolder(tag, data);
	}

	@Override
	public String getCustomGlobalPlaceHolder(String tag, String arg, Object data) {
		switch (tag) {
		case "careas":
			if (isPlayerOnline(arg))
				return ALtoString(getAreas(getPlayerOnline(arg).getPosition()));
			break;
		}
		return super.getCustomGlobalPlaceHolder(tag, arg, data);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals(CT.getMessage(getName(), "label"))) {
			if (sender.isPlayer()) {
				FormWindowSimple window = new FormWindowSimple(getName(), "사용할 기능을 클릭해주세요.");
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
			SimpleDialog.sendDialog(this, "form_add_area", event.getPlayer(), window);
		} else if (text.equals("영역 편집")) {
			SimpleDialog.sendDialog(this, "form_edit_select_world", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(areas.getKeys(false)));
		} else if (text.equals("영역 삭제")) {
			SimpleDialog.sendDialog(this, "form_delete_selete_world", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(areas.getKeys(false)));
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

		HashMap<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("script", script);
		queue.put(event.getPlayer(), map);
		SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "첫번째와 두번째 지점을 지정해주세요.");
	}

	HashMap<String, String> selectedWorld = new HashMap<>();

	public void form_edit_select_world(PlayerFormRespondedEvent event, Object data) {
		String world = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		selectedWorld.put(event.getPlayer().getName(), world);
		SimpleDialog.sendDialog(this, "form_edit_select_area", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(areas.getSection(world).getKeys(false)));
	}

	public void form_edit_select_area(PlayerFormRespondedEvent event, Object data) {
		String area = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		FormWindowCustom window = new FormWindowCustom("구역 수정");
		window.addElement(new ElementLabel("구역을 편집합니다."));
		window.addElement(new ElementInput("스크립트", "입력해주세요.", getScript(selectedWorld.get(event.getPlayer().getName()), area)));
		window.addElement(new ElementLabel("월드 : " + areas.getString(selectedWorld.get(event.getPlayer().getName()) + "." + area + ".world")));
		window.addElement(new ElementLabel("1좌표 : " + areas.getString(selectedWorld.get(event.getPlayer().getName()) + "." + area + ".fstPos")));
		window.addElement(new ElementLabel("2좌표 : " + areas.getString(selectedWorld.get(event.getPlayer().getName()) + "." + area + ".sndPos")));
		SimpleDialog.sendDialog(this, "form_edit_area", event.getPlayer(), window, selectedWorld.get(event.getPlayer().getName()) + "." + area);
	}

	public void form_edit_area(PlayerFormRespondedEvent event, Object data) {
		String script = ((FormResponseCustom) event.getResponse()).getInputResponse(1);
		areas.set(((String) data) + ".script", script);
		areas.save();
		SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "수정되었습니다.");
	}

	public void form_delete_selete_world(PlayerFormRespondedEvent event, Object data) {
		String world = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		selectedWorld.put(event.getPlayer().getName(), world);
		SimpleDialog.sendDialog(this, "form_delete_select_area", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(areas.getSection(world).getKeys(false)));
	}

	public void form_delete_select_area(PlayerFormRespondedEvent event, Object data) {
		String area = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		areas.getSection(selectedWorld.get(event.getPlayer().getName())).remove(area);
		areas.save();
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
	HashMap<Player, HashMap<String, Object>> queue = new HashMap<>();

	@Override
	public void event(Event e) {
		if (e instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent) e;

			if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
				return;

			if (restrictCPS(event))// TODO REMOVE #1
				return;

			if (queue.containsKey(event.getPlayer())) {
				if (!queue.get(event.getPlayer()).containsKey("fstPos")) {// save first pos
					queue.get(event.getPlayer()).put("fstPos", event.getBlock());
					SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "첫번째 지점이 저장되었습니다.\n두번째 지점을 클릭해주세요.");
				} else {// second pos
					String name = (String) queue.get(event.getPlayer()).get("name");
					String script = (String) queue.get(event.getPlayer()).get("script");
					String world = event.getBlock().getLevel().getName();
					Position fstPos = (Position) queue.get(event.getPlayer()).get("fstPos");
					Position sndPos = event.getBlock();
					if (fstPos.getLevel() != sndPos.getLevel()) {
						SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "첫번째 지점과 두번째 지점의 월드는 같아야합니다.");
						queue.remove(event.getPlayer());
						return;
					}

					areas.set(world + "." + name + ".name", name);
					areas.set(world + "." + name + ".script", script);
					areas.set(world + "." + name + ".world", fstPos.getLevel().getName());
					areas.set(world + "." + name + ".fstPos", fstPos.getFloorX() + "," + fstPos.getFloorY() + "," + fstPos.getFloorZ());
					areas.set(world + "." + name + ".sndPos", sndPos.getFloorX() + "," + sndPos.getFloorY() + "," + sndPos.getFloorZ());
					areas.save();

					SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "영역이 저장되었습니다.");

					queue.remove(event.getPlayer());
				}
			} else {
				Position pos = event.getBlock().getLocation().clone();
				ArrayList<String> list = getAreas(pos);

				for (String area : list)
					CustomizerExecutor.executeScript(this, event.getPlayer(), CustomizerScript.getScript(getScript(pos.level.getName(), area)), event, area);
			}
		}
	}

	//////////////////////////////////

	public ArrayList<String> getAreas(Position pos) {
		ArrayList<String> list = new ArrayList<>();

		ConfigSection ar = areas.getSection(pos.getLevel().getName());
		for (String key : ar.getKeys(false)) {
			String[] coord = ar.getString(key + ".fstPos").split(",");
			int[] fstPos = new int[] { Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2]) };
			coord = ar.getString(key + ".sndPos").split(",");
			int[] sndPos = new int[] { Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2]) };

			int minX = Math.min(fstPos[0], sndPos[0]);
			int maxX = Math.max(fstPos[0], sndPos[0]);
			int minY = Math.min(fstPos[1], fstPos[1]);
			int maxY = Math.max(fstPos[1], fstPos[2]);
			int minZ = Math.min(fstPos[2], fstPos[2]);
			int maxZ = Math.max(fstPos[2], sndPos[2]);

			if (minX <= pos.getX() && pos.getX() <= maxX)
				if (minY <= pos.getY() && pos.getY() <= maxY)
					if (minZ <= pos.getZ() && pos.getZ() <= maxZ)
						list.add(key);
		}

		if (list.size() == 0)
			list.add("UNKNOWN");
		return list;
	}

	public String getScript(String world, String name) {
		return areas.getString(world + "." + name + ".script");
	}
}
