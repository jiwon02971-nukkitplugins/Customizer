package iKguana.customizer.interfaces;

import java.util.ArrayList;
import java.util.Date;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.PlaySoundPacket;
import iKguana.profiler.Profiler;

public abstract class RFC {
	protected static boolean isInteger(String... nums) {
		try {
			for (String num : nums)
				if (Integer.parseInt(num) == Double.parseDouble(num))
					return true;
		} catch (Exception err) {
		}
		return false;
	}

	protected static boolean isLong(String num) {
		try {
			Long.parseLong(num);
			return true;
		} catch (Exception err) {
		}
		return false;
	}

	protected static boolean isFloat(String... nums) {
		try {
			for (String num : nums)
				Float.parseFloat(num);
			return true;
		} catch (Exception err) {
		}
		return false;
	}

	protected static boolean isDouble(String num) {
		try {
			Double.parseDouble(num);
			return true;
		} catch (Exception err) {
		}
		return false;
	}

	protected static String replaceAll(String str, String[] args) {
		for (int i = 0; i < args.length; i++)
			str = str.replace("%" + (i + 1), args[i]);
		return str;
	}

	protected static <T> String ALtoString(ArrayList<T> al) {
		return ALtoString(al, ", ");
	}

	protected static <T> String ALtoString(ArrayList<T> al, String join) {
		String str = "";
		for (T t : al)
			str += t + join;
		if (al.size() > 0)
			str = str.substring(0, str.lastIndexOf(join));
		return str;
	}

	private static ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");

	protected static boolean runStatements(String syntax) {
		try {
			return (Boolean) engine.eval(syntax);
		} catch (Exception e) {
			return false;
		}
	}

	protected static float calculate(String syntax) {
		try {
			ScriptEngine engine = (new ScriptEngineManager()).getEngineByName("JavaScript");
			return (Float) engine.eval(syntax);
		} catch (Exception e) {
			return -99;
		}
	}

	protected static boolean isPlayer(String name) {
		return Profiler.getInstance().isPlayerRegistered(name);
	}

	protected static boolean isPlayerOnline(String name) {
		for (Player player : Server.getInstance().getOnlinePlayers().values())
			if (player.getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

	protected static String getExactName(String name) {
		return Profiler.getInstance().getExactName(name);
	}

	protected static Player getPlayerOnline(String name) {
		for (Player player : Server.getInstance().getOnlinePlayers().values())
			if (player.getName().equalsIgnoreCase(name))
				return player;
		return null;
	}

	protected static boolean isLevel(String name) {
		for (Level lvl : Server.getInstance().getLevels().values())
			if (lvl.getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

	protected static Level getLevel(String name) {
		for (Level lvl : Server.getInstance().getLevels().values())
			if (lvl.getName().equalsIgnoreCase(name))
				return lvl;
		return null;
	}

	protected static boolean isSound(String sound) {
		for (Sound snd : Sound.values())
			if (sound.equals(snd.getSound()))
				return true;
		return false;
	}

	protected static void playSound(Player player, String sound) {
		PlaySoundPacket pk = new PlaySoundPacket();
		pk.volume = 1;
		pk.name = sound;

		pk.x = player.getFloorX();
		pk.y = player.getFloorY();
		pk.z = player.getFloorZ();

		pk.pitch = 0;

		player.dataPacket(pk);
	}

	protected static long getTime() {
		return new Date().getTime();
	}

	protected static void sendError(Player player, String cause) {
		player.sendMessage("오류가 발생했습니다. (" + cause + ")");
	}
}
