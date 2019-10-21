package iKguana.customizer;

import java.io.File;

import cn.nukkit.utils.Config;
import iKguana.customizer.interfaces.UsefulFunctions;
import iKguana.profiler.Profiler;

public class CustomizerVariable extends UsefulFunctions {
	static Config globalVars;

	public CustomizerVariable() {
		globalVars = new Config(Customizer.getInstance().getDataFolder() + File.separator + "CustomizerGlobalVariables.yml");
	}

	public static String getPDFS(String player, String key) {
		return getPDFS(player + "/" + key);
	}

	public static String getPDFS(String playerkey) {// PlayerDataForScript playerkey : <String: player>/<String: key>
		if (playerkey.indexOf("/") != -1) {
			String player = playerkey.substring(0, playerkey.indexOf("/")).trim();
			String key = playerkey.substring(playerkey.indexOf("/") + 1, playerkey.length()).trim();
			if (player.length() != 0 && key.length() != 0)
				if (isPlayer(player))
					return Profiler.getInstance().open(player).getString("CustomizerVar." + key);
		}
		return null;
	}

	public static void setPD(String player, String key, String data) {
		if (isPlayer(player))
			if (key.trim().length() != 0 && data.trim().length() != 0)
				Profiler.getInstance().open(getExactName(player)).set("CustomizerVar." + key, data);
	}

	public static String getGDFS(String key) {// GlobalDataForScript
		if (key.trim().length() != 0) {
			return globalVars.getString(key);
		}
		return null;
	}

	public static void setGD(String key, String data) {
		if (key.trim().length() != 0) {
			globalVars.set(key, data);
			globalVars.save();
		}
	}
}
