package iKguana.customizer.tools;

import cn.nukkit.utils.Config;
import iKguana.customizer.Customizer;

public class CT {
	private static Customizer $plugin;

	public CT(Customizer plugin) {
		$plugin = plugin;
		plugin.saveDefaultConfig();
	}

	public static Config getConfig() {
		return $plugin.getConfig();
	}

	public static String getMessage(String name, String key) {
		return getMessage(name, key, new String[] {});
	}

	public static String getMessage(String name, String key, String... strings) {
		String str = $plugin.getConfig().getString("strings." + name + "." + key);
		for (int i = 0; i < strings.length; i++)
			str = str.replace("%" + (i + 1), strings[i]);
		return str;
	}
}
