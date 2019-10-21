package iKguana.customizer;

import cn.nukkit.Server;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.PluginCommand;
import iKguana.customizer.commands.CustomizerArea;
import iKguana.customizer.commands.CustomizerClick;
import iKguana.customizer.commands.CustomizerCommand;
import iKguana.customizer.commands.CustomizerDialog;
import iKguana.customizer.commands.CustomizerEntity;
import iKguana.customizer.commands.CustomizerEvent;
import iKguana.customizer.commands.CustomizerScript;

public class CustomizerCommands {
	private static CustomizerCommands $instance;

	public CustomizerCommands() {
		$instance = this;

		registerCommands();
	}

	public void registerCommands() {
		new CustomizerArea();
		new CustomizerClick();
		new CustomizerCommand();
		new CustomizerDialog();
		new CustomizerEntity();
		new CustomizerEvent();
		new CustomizerScript();
	}

	public void registerCommand(CommandExecutor executor, String name, String description, String usage, String permission) {
		PluginCommand<Customizer> cmd = new PluginCommand<Customizer>(name, Customizer.getInstance());
		cmd.setDescription(description);
		cmd.setUsage(usage);
		cmd.setExecutor(executor);
		cmd.setPermission(permission);
		Server.getInstance().getCommandMap().register("Helper", cmd);
	}

	public static CustomizerCommands getInstance() {
		return $instance;
	}
}
