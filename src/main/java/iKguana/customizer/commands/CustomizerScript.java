package iKguana.customizer.commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.jline.utils.InputStreamReader;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Event;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import iKguana.customizer.Customizer;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.CustomizerExecutor;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;
import iKguana.simpledialog.SimpleDialog;

public class CustomizerScript extends CustomizerBase {
	private static File scriptFolder;

	public CustomizerScript() {
		setName("CustomizerScript");

		scriptFolder = new File(Customizer.getInstance().getDataFolder() + File.separator + "Scripts");
		scriptFolder.mkdirs();

		CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));

		CustomizerExecutor.customGlobalFunctions.put("@RUNSCRIPT", this);
	}

	@Override
	public void executeCustomGlobalFunction(CustomizerBase cb, Player player, Event event, String line, String fc, String arg, String[] args) {
		if (fc.equals("@RUNSCRIPT")) {
			if (args.length > 0)
				CustomizerExecutor.executeScript(cb, player, args[0], CustomizerScript.getSources(args[0]), event, null);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals(CT.getMessage(getName(), "label"))) {
			if (sender.isPlayer()) {
				FormWindowSimple window = new FormWindowSimple("커스텀 스크립트", "사용할 기능을 클릭해주세요.");
				window.addButton(new ElementButton("스크립트 추가"));
				window.addButton(new ElementButton("스크립트 보기"));
				window.addButton(new ElementButton("스크립트 편집"));
				window.addButton(new ElementButton("스크립트 삭제"));
				SimpleDialog.sendDialog(this, "form_script_menu", (Player) sender, window);
			} else
				sender.sendMessage(CT.getMessage("__Public", "NOT_IN_GAME"));
			return true;
		}
		return false;
	}

	public void form_script_menu(PlayerFormRespondedEvent event, Object data) {
		String text = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		if (text.equals("스크립트 추가")) {
			FormWindowCustom window = new FormWindowCustom("스크립트 추가");
			window.addElement(new ElementInput("스크립트의 이름을 입력해주세요."));
			window.addElement(new ElementInput("소스를 입력해주세요."));
			window.addElement(new ElementInput("소스를 입력해주세요.", "선택 입력입니다. 공백일시 무시됨."));
			SimpleDialog.sendDialog(this, "form_addSource", event.getPlayer(), window);
		} else if (text.equals("스크립트 보기")) {
			SimpleDialog.sendDialog(this, "form_showScript", event.getPlayer(), SimpleDialog.Type.FILTERING, getAllScripts());
		} else if (text.equals("스크립트 편집")) {
			SimpleDialog.sendDialog(this, "form_editScript", event.getPlayer(), SimpleDialog.Type.FILTERING, getAllScripts());
		} else if (text.equals("스크립트 삭제")) {
			SimpleDialog.sendDialog(this, "form_removeScript", event.getPlayer(), SimpleDialog.Type.FILTERING, getAllScripts());
		}
	}

	public void form_addSource(PlayerFormRespondedEvent event, Object data) {
		FormResponseCustom response = (FormResponseCustom) event.getResponse();
		String name = response.getInputResponse(0);
		String source_0 = response.getInputResponse(1);
		String source_1 = response.getInputResponse(2);
		if (name.trim().length() != 0) {
			addSource(name, source_0);
			if (source_1.trim().length() != 0)
				addSource(name, source_1);
			
			SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "소스가 정상적으로 추가되었습니다.");
		} else
			SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "스크립트의 이름은 공백이 될수없습니다.");
	}

	public void form_showScript(PlayerFormRespondedEvent event, Object data) {
		String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, getSourcesString(name));
	}

	public void form_editScript(PlayerFormRespondedEvent event, Object data) {
		String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		FormWindowCustom window = new FormWindowCustom("스크립트 수정");
		for (String source : getSources(name))
			window.addElement(new ElementInput("", "", source));
		SimpleDialog.sendDialog(this, "form_applySource", event.getPlayer(), window, name);
	}

	public void form_applySource(PlayerFormRespondedEvent event, Object data) {
		FormResponseCustom response = (FormResponseCustom) event.getResponse();
		for (int i = 0; i < response.getResponses().keySet().size(); i++)
			editLine((String) data, i, response.getInputResponse(i));
		SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "수정되었습니다.");
	}

	public void form_removeScript(PlayerFormRespondedEvent event, Object data) {
		String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();

		removeScript(name);

		SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "스크립트가 제거되었습니다.");
	}

//////////////////////////////////////////////////////////////////////////////////
	public static File getScriptFile(String name) {
		return new File(scriptFolder + File.separator + name + ".is");
	}

	public static void addSource(String name, String source) {
		File script = getScriptFile(name);
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(script, true)), true);
			writer.println(source);
			writer.close();
		} catch (Exception err) {
			Server.getInstance().getLogger().debug("Customizer] 오류가 발생했습니다. (" + err.getClass().getName() + ")");
		}
	}

	public static void editLine(String name, int idx, String line) {
		ArrayList<String> lines = getSources(name);
		if (0 <= idx && idx < lines.size())
			lines.set(idx, line);
		else
			return;

		removeScript(name);

		File script = getScriptFile(name);
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(script, true)), true);
			for (String l : lines)
				writer.println(l);
			writer.close();
		} catch (Exception err) {
			Server.getInstance().getLogger().debug("Customizer] 오류가 발생했습니다. (" + err.getClass().getName() + ")");
		}
	}

	public static String getSourcesString(String name) {
		String str = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getScriptFile(name)), "UTF-8"));

			String line = "";
			while ((line = reader.readLine()) != null)
				str += line + "\n";

			reader.close();
		} catch (Exception err) {
			Server.getInstance().getLogger().debug("Customizer] 오류가 발생했습니다. (" + err.getClass().getName() + ")");
		}
		return str;
	}

	public static ArrayList<String> getSources(String name) {
		ArrayList<String> list = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getScriptFile(name)), "UTF-8"));

			String line = "";
			while ((line = reader.readLine()) != null)
				list.add(line);

			reader.close();
		} catch (Exception err) {
			Server.getInstance().getLogger().debug("Customizer] 오류가 발생했습니다. (" + err.getClass().getName() + ")");
		}
		return list;
	}

	public static void removeSource(String name, int idx) {
		ArrayList<String> lines = getSources(name);
		lines.remove(idx);

		removeScript(name);

		File script = getScriptFile(name);
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(script, true)), true);
			for (String line : lines)
				writer.println(line);
			writer.close();
		} catch (Exception err) {
			Server.getInstance().getLogger().debug("Customizer] 오류가 발생했습니다.");
		}
	}

	public static void removeScript(String name) {
		File script = getScriptFile(name);
		if (script.isFile())
			script.delete();
	}

	public static ArrayList<String> getAllScripts() {
		ArrayList<String> list = new ArrayList<>();
		for (File is : scriptFolder.listFiles())
			if (is.getName().endsWith(".is"))
				list.add(is.getName().replace(".is", ""));
		return list;
	}
}
