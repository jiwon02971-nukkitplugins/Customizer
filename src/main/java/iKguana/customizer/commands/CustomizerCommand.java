package iKguana.customizer.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Event;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import iKguana.customizer.Customizer;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.CustomizerEvents;
import iKguana.customizer.CustomizerExecutor;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;
import iKguana.simpledialog.SimpleDialog;

public class CustomizerCommand extends CustomizerBase {
	public static File CFG_PATH;

	public CustomizerCommand() {
		setName("CustomizerCommand");

		CFG_PATH = new File(Customizer.getInstance().getDataFolder() + File.separator + getName() + ".yml");
		new CommandManager(CFG_PATH);

		CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));
		CustomizerEvents.playerCommandPreprocessEvent.add(this);

		CustomizerExecutor.customGlobalFunctions.put("@CMD", this);
		CustomizerExecutor.customGlobalFunctions.put("@CMDNC", this);
		CustomizerExecutor.customGlobalFunctions.put("@CMDOP", this);
		CustomizerExecutor.customGlobalFunctions.put("@CMDOPNC", this);
		CustomizerExecutor.customGlobalFunctions.put("@CMDCON", this);
	}

	@Override
	public String getCustomPlaceHolder(String tag, Object data) {
		String cmdlabel = ((String) data).split(" ")[0];

		switch (tag) {
		case "cmdlabel":
			return cmdlabel;
		case "cmdarg":
			return ((String) data).replace(cmdlabel, "").trim();
		default:
			return super.getCustomPlaceHolder(tag, data);
		}
	}

	@Override
	public String getCustomPlaceHolder(String tag, String arg, Object data) {
		String cmdlabel = ((String) data).split(" ")[0];
		String[] cmdargs = ((String) data).replace(cmdlabel, "").trim().split(" ");

		switch (tag) {
		case "cmdargs":
			if (isInteger(arg) && 0 <= Integer.parseInt(arg) && Integer.parseInt(arg) < cmdargs.length)
				return cmdargs[Integer.parseInt(arg)];
		}
		return super.getCustomPlaceHolder(tag, arg, data);
	}

	@Override
	public boolean executeCustomFunction(Player player, String line, String fc, String arg, String[] args) {
		return super.executeCustomFunction(player, line, fc, arg, args);
	}

	@Override
	public void executeCustomGlobalFunction(CustomizerBase cb, Player player, Event event, String line, String fc, String arg, String[] args) {
		if (fc.equals("@CMD")) {
			String cmdlabel = ((String) arg).split(" ")[0];
			if (CommandManager.getIt().isRegistered(cmdlabel))
				CustomizerExecutor.executeScript(this, player, CommandManager.getIt().getScript(cmdlabel), CustomizerScript.getSources(CommandManager.getIt().getScript(cmdlabel)), event, arg);
			else
				Server.getInstance().getCommandMap().dispatch(player, arg);
		} else if (fc.equals("@CMDNC")) {
			Server.getInstance().getCommandMap().dispatch(player, arg);
		} else if (fc.equals("@CMDOP")) {
			boolean isOp = player.isOp();
			player.setOp(true);
			String cmdlabel = ((String) arg).split(" ")[0];
			if (CommandManager.getIt().isRegistered(cmdlabel))
				CustomizerExecutor.executeScript(this, player, CommandManager.getIt().getScript(cmdlabel), CustomizerScript.getSources(CommandManager.getIt().getScript(cmdlabel)), event, arg);
			else
				Server.getInstance().getCommandMap().dispatch(player, arg);
			player.setOp(isOp);
		} else if (fc.equals("@CMDOPNC")) {
			boolean isOp = player.isOp();
			player.setOp(true);
			Server.getInstance().getCommandMap().dispatch(player, arg);
			player.setOp(isOp);
		} else if (fc.equals("@CMDCON")) {
			Server.getInstance().getCommandMap().dispatch(Server.getInstance().getConsoleSender(), arg);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals(CT.getMessage(getName(), "label"))) {
			if (sender.isPlayer()) {
				FormWindowSimple window = new FormWindowSimple("커스텀 명령어", "사용하고싶은 기능을 클릭해주세요.");
				window.addButton(new ElementButton("명령어 추가"));
				window.addButton(new ElementButton("명령어 확인"));
				window.addButton(new ElementButton("명령어 삭제"));
				SimpleDialog.sendDialog(this, "form_cmd_menu", (Player) sender, window);
			} else
				sender.sendMessage(CT.getMessage("__Public", "NOT_IN_GAME"));
			return true;
		}
		return false;
	}

	public void form_cmd_menu(PlayerFormRespondedEvent event, Object data) {
		String text = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		if (text.equals("명령어 추가")) {
			FormWindowCustom window = new FormWindowCustom("명령어 추가");
			window.addElement(new ElementInput("명령어의 라벨을 입력해주세요."));
			window.addElement(new ElementInput("사용할 스크립트를 입력해주세요."));
			SimpleDialog.sendDialog(this, "form_addCmd", event.getPlayer(), window);
		} else if (text.equals("명령어 확인")) {
			SimpleDialog.sendDialog(this, "form_checkCmd", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(CommandManager.getIt().getLabels()));
		} else if (text.equals("명령어 삭제")) {
			SimpleDialog.sendDialog(this, "form_deleteCmd", event.getPlayer(), SimpleDialog.Type.FILTERING, new ArrayList<String>(CommandManager.getIt().getLabels()));
		}
	}

	public void form_addCmd(PlayerFormRespondedEvent event, Object data) {
		String label = ((FormResponseCustom) event.getResponse()).getInputResponse(0);
		String script = ((FormResponseCustom) event.getResponse()).getInputResponse(1);

		CommandManager.getIt().set(label, script);

		SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "추가되었습니다. (" + label + " : " + script + ")");
	}

	public void form_checkCmd(PlayerFormRespondedEvent event, Object data) {
		String label = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
		String str = "";
		str += label + "명령어는 " + CommandManager.getIt().getScript(label) + " 스크립트를 사용합니다.\n\n";
		str += CustomizerScript.getSourcesString(CommandManager.getIt().getScript(label));

		SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, str);
	}

	public void form_deleteCmd(PlayerFormRespondedEvent event, Object data) {
		String label = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();

		CommandManager.getIt().remove(label);

		SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "스크립트 제거에 성공하였습니다.");
	}

	@Override
	public void event(Event e) {
		if (e instanceof PlayerCommandPreprocessEvent) {
			PlayerCommandPreprocessEvent event = (PlayerCommandPreprocessEvent) e;

			if (event.getMessage().startsWith("/")) {
				String label = event.getMessage().substring(1, event.getMessage().indexOf(" ") != -1 ? event.getMessage().indexOf(" ") : event.getMessage().length());

				if (CommandManager.getIt().isRegistered(label)) {
					CustomizerExecutor.executeScript(this, event.getPlayer(), CommandManager.getIt().getScript(label), CustomizerScript.getSources(CommandManager.getIt().getScript(label)), event, event.getMessage().substring(1).trim());
					event.setCancelled();
				}
			}
		}
	}
}

class CommandManager {
	private static CommandManager $instance;
	private Config cfg;

	public CommandManager(File cfgpath) {
		$instance = this;
		cfg = new Config(cfgpath, Config.YAML);
	}

	public void set(String label, String script) {
		cfg.set(label.toLowerCase(), script);
		cfg.save();
	}

	public boolean isRegistered(String label) {
		return cfg.exists(label.toLowerCase());
	}

	public Set<String> getLabels() {
		return cfg.getKeys();
	}

	public String getScript(String label) {
		return cfg.getString(label.toLowerCase());
	}

	public void remove(String label) {
		cfg.remove(label.toLowerCase());
		cfg.save();
	}

	public Config getConfig() {
		return cfg;
	}

	public static CommandManager getIt() {
		return $instance;
	}
}
