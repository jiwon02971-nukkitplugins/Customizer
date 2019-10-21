package iKguana.customizer.interfaces;

import cn.nukkit.Player;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.event.Event;
import iKguana.customizer.CustomizerExecutor;

public abstract class CustomizerBase extends UsefulFunctions implements CommandExecutor {
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void event(Event event) {
	}

	public boolean executeCustomFunction(Player player, String line, String fc, String arg, String[] args) {
		return false;
	}

	public void executeCustomGlobalFunction(CustomizerBase cb,Player player,Event event, String line, String fc, String arg, String[] args) {
	}

	public String getCustomPlaceHolder(String tag, Object data) {
		return CustomizerExecutor.NULL;
	}

	public String getCustomGlobalPlaceHolder(String tag, Object data) {
		return CustomizerExecutor.NULL;
	}

	public String getCustomPlaceHolder(String tag, String arg, Object data) {
		return CustomizerExecutor.NULL;
	}

	public String getCustomGlobalPlaceHolder(String tag, String arg, Object data) {
		return CustomizerExecutor.NULL;
	}
}
