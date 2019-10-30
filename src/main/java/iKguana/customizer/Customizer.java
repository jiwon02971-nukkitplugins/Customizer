package iKguana.customizer;

import java.util.ArrayList;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import iKguana.customizer.tools.CT;

public class Customizer extends PluginBase {
	private static Customizer $instance;

	@Override
	public void onEnable() {
		$instance = this;

		getDataFolder().mkdirs();
		
		new CustomizerVariable();
		new CT(this);
		new CustomizerCommands();
		getServer().getPluginManager().registerEvents(new CustomizerEvents(), this);
	}

	public static Customizer getInstance() {
		return $instance;
	}
}
