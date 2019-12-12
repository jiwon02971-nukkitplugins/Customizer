package iKguana.customizer;

import java.text.SimpleDateFormat;
import java.util.*;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Event;
import cn.nukkit.scheduler.Task;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.interfaces.RFC;
import iKguana.customizer.interfaces.ScriptBase;
import iKguana.economy.MoneyAPI;

public class CustomizerExecutor extends RFC {
    public static void executeScript(CustomizerBase cb, Player player, ScriptBase script, Event event, Object data) {
        executeScript(cb, player, script, event, data, NULL);
    }

    public static void executeScript(CustomizerBase cb, Player player, ScriptBase script, Event event, Object data, String tag) {
        (new ScriptTask(cb, player, script, event, data)).r();
    }

    public static HashMap<String, CustomizerBase> customGlobalPlaceHolders = new HashMap<>();
    public static HashMap<String, CustomizerBase> customGlobalPlaceHolders_ = new HashMap<>();

    public static String NULL = "NULL";

    public static String parsePlaceHolder(CustomizerBase cb, Player player, String script, Event event, Object data) {
        String str = script.trim();
        String ph = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '<')
                ph = " ";
            else if (!ph.equals("") && str.charAt(i) != '>')
                ph += str.charAt(i);
            else if (!ph.equals("") && str.charAt(i) == '>') {
                ph = ph.trim();
                if (ph.indexOf(":") == -1) {
                    switch (ph) {
                        case "playername":
                            str = str.replace(s(ph), player.getName());
                            break;
                        case "eventname":
                            str = str.replace(s(ph), event != null ? event.getClass().getSimpleName() : NULL);
                            break;
                        case "motd":
                            str = str.replace(s(ph), Server.getInstance().getNetwork().getName());
                            break;
                        default:
                            if (customGlobalPlaceHolders.containsKey(ph))
                                str = str.replace(s(ph), customGlobalPlaceHolders.get(ph).getCustomGlobalPlaceHolder(ph, data));
                            else
                                str = str.replace(s(ph), (data != null ? cb.getCustomPlaceHolder(ph, data) : NULL));
                    }
                } else {
                    String before = ph.substring(0, ph.indexOf(":"));
                    String after = ph.substring(ph.indexOf(":") + 1);
                    switch (before) {
                        case "isop":
                            if (isPlayerOnline(after))
                                str = str.replace(s(ph), String.valueOf(Server.getInstance().getPlayerExact(after).isOp()));
                            else
                                str = str.replace(s(ph), String.valueOf(false));
                            break;
                        case "health":
                            if (isPlayerOnline(after))
                                str = str.replace(s(ph), String.valueOf(Server.getInstance().getPlayerExact(after).getHealth()));
                            else
                                str = str.replace(s(ph), NULL);
                            break;
                        case "maxhealth":
                            if (isPlayerOnline(after))
                                str = str.replace(s(ph), String.valueOf(Server.getInstance().getPlayerExact(after).getMaxHealth()));
                            else
                                str = str.replace(s(ph), NULL);
                            break;
                        case "hungry":
                            if (isPlayerOnline(after))
                                str = str.replace(s(ph), String.valueOf(Server.getInstance().getPlayerExact(after).getFoodData().getLevel()));
                            else
                                str = str.replace(s(ph), String.valueOf(false));
                            break;
                        case "xp":
                            if (isPlayerOnline(after))
                                str = str.replace(s(ph), String.valueOf(Server.getInstance().getPlayerExact(after).getExperience()));
                            else
                                str = str.replace(s(ph), NULL);
                            break;
                        case "gamemode":
                            if (isPlayerOnline(after))
                                str = str.replace(s(ph), String.valueOf(Server.getInstance().getPlayerExact(after).getGamemode()));
                            else
                                str = str.replace(s(ph), String.valueOf(false));
                            break;

                        case "leveltime":
                            if (isLevel(after))
                                str = str.replace(s(ph), String.valueOf(getLevel(after)));
                            else
                                str = str.replace(s(ph), NULL);
                            break;
                        case "realtime":
                            str = str.replace(s(ph), (new SimpleDateFormat(after)).format(new Date()));
                            break;
                        case "calculate":
                            str = str.replace(s(ph), String.valueOf(calculate(after)));
                            break;

                        //External Plugin METHOD
                        case "money":
                            if (isPlayerOnline(after))
                                str = str.replace(s(ph), String.valueOf(MoneyAPI.getInstance().getMoney(Server.getInstance().getPlayerExact(after).getName())));
                            else
                                str = str.replace(s(ph), NULL);
                            break;

                        case "pvar":
                            if (CustomizerVariable.getPDFS(after) != null)
                                str = str.replace(s(ph), CustomizerVariable.getPDFS(after));
                            else
                                str = str.replace(s(ph), NULL);
                            break;
                        case "gvar":
                            str = str.replace(s(ph), CustomizerVariable.getGDFS(after));
                            break;

                        default:
                            if (customGlobalPlaceHolders_.containsKey(before))
                                str = str.replace(s(ph), customGlobalPlaceHolders_.get(before).getCustomGlobalPlaceHolder(before, after, data));
                            else
                                str = str.replace(s(ph), (cb != null ? cb.getCustomPlaceHolder(before, after, data) : NULL));
                    }
                }
                i = 0;
                ph = "";
            }
        }
        return str;
    }

    public static String s(String str) {
        return "<" + str + ">";
    }

    public static HashMap<String, CustomizerBase> customGlobalFunctions = new HashMap<>();

    public static int runFunction(CustomizerBase cb, ScriptTask task, String script, Player player, Event event, String source) {
        String fc = source.split(" ")[0];
        String arg = source.replace(fc, "").trim();
        String[] args = arg.split(" ");

        try {
            if (source.trim().length() == 0)
                return 0;

            if (task.localvars.containsKey("flag") && !(fc.equals("@ELSE") || fc.equals("@RESETIF")))
                return 0;

            switch (fc) {//TODO 메소드 완성, 추가
                case "@ENDSCRIPT":
                    return -1;

                case "@IF":
                    if (args.length >= 2)
                        if (args[0].startsWith("#")) {
                            if (!runStatements(arg.replace(args[0], "").trim()))
                                task.localvars.put("flag", args[0]);
                        } else
                            sendError(player, "TAG_MUST_START_WITH_#");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@ELSE":
                    if (!task.localvars.containsKey("flag"))
                        return -1;
                    if (args.length >= 1) {
                        if (args[0].equals((String) task.localvars.get("flag")))
                            task.localvars.remove("flag");
                    } else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@ENDIF":
                    if (args.length >= 1) {
                        if (args[0].equals((String) task.localvars.get("flag")))
                            task.localvars.remove("flag");
                    } else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;

                case "@DELAY":
                    if (args.length > 0 && isInteger(args[0]))
                        return Integer.parseInt(args[0]);
                    break;
                case "@COOLDOWN":
                    if (args.length > 0 && isInteger(args[0]))
                        CustomizerVariable.setPD(player.getName(), "cooldowns." + script, String.valueOf(getTime() + (Integer.parseInt(args[0]) * 1000)));
                    break;

                case "@MESSAGE":
                    player.sendMessage(arg);
                    break;
                case "@BROADCAST":
                    Server.getInstance().broadcastMessage(arg);
                    break;

                case "@SOUND":
                    if (args.length >= 2)
                        if (isPlayerOnline(args[0]) && isSound(args[1]))
                            playSound(getPlayerOnline(args[0]), args[1]);
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@EFFECT":
                    break;

                case "@DROPITEM":
                    break;

                case "@TELEPORT":
                    break;

                case "@HEAL":
                    if (args.length >= 2)
                        if (isPlayerOnline(args[0]) && isInteger(args[1]))
                            getPlayerOnline(args[0]).heal(Integer.parseInt(args[1]));
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@SETHEALTH":
                    if (args.length >= 2)
                        if (isPlayerOnline(args[0]) && isInteger(args[1]))
                            getPlayerOnline(args[0]).setHealth(Integer.parseInt(args[1]));
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@KILL":
                    if (args.length >= 1)
                        if (isPlayerOnline(args[0]))
                            getPlayerOnline(args[0]).kill();
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@SETHUNGRY":
                    if (args.length >= 2)
                        if (isPlayerOnline(args[0]) && isInteger(args[1]))
                            getPlayerOnline(args[0]).getFoodData().setLevel(Integer.parseInt(args[1]));
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;

                case "@JUMP":
                    if (args.length >= 4)
                        if (isPlayerOnline(args[0]) && isDouble(args[1]) && isDouble(args[2]) && isDouble(args[3]))
                            Server.getInstance().getPlayer(args[0]).addMotion(Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;

                // EXTENAL PLUGIN
                // ECONOMY
                case "@ADDMONEY":
                    if (args.length >= 2)
                        if (isPlayerOnline(args[0]) && isInteger(args[1]))
                            MoneyAPI.getInstance().addMoney(getPlayerOnline(args[0]).getName(), Integer.parseInt(args[1]));
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@SETMONEY":
                    if (args.length >= 2)
                        if (isPlayerOnline(args[0]) && isInteger(args[1]))
                            MoneyAPI.getInstance().setMoney(getPlayerOnline(args[0]).getName(), Integer.parseInt(args[1]));
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                case "@TAKEMONEY":
                    if (args.length >= 2)
                        if (isPlayerOnline(args[0]) && isInteger(args[1]))
                            MoneyAPI.getInstance().takeMoney(getPlayerOnline(args[0]).getName(), Integer.parseInt(args[1]));
                        else
                            sendError(player, "INCORRECT_ARGS");
                    else
                        sendError(player, "NOT_ENOUGH_ARGS");
                    break;
                // ECONOMY END

                // PROFILER
                case "@SETPVAR":
                    if (args.length >= 3)
                        if (isPlayer(args[0]) && args[1].trim().length() != 0 && args[2].trim().length() != 0)
                            CustomizerVariable.setPD(args[0], args[1], args[2]);
                    break;
                case "@SETGVAR":
                    if (args.length >= 2)
                        if (args[0].trim().length() != 0 && args[1].trim().length() != 0)
                            CustomizerVariable.setGD(args[0], args[1]);
                    break;
                // PROFILER END
                // EXTERNAL PLUGIN END

                case "@SETCANCELLED":
                    if (args.length > 0)
                        event.setCancelled(Boolean.parseBoolean(args[0]));
                    break;

                default:
                    if (customGlobalFunctions.containsKey(fc)) {
                        customGlobalFunctions.get(fc).executeCustomGlobalFunction(cb, player, event, source, fc, arg, args);
                        break;
                    }
                    if (!cb.executeCustomFunction(player, source, fc, arg, args))
                        sendError(player, "UNKNOWN_METHOD");
            }
        } catch (Exception err) {
            sendError(player, err.getClass().getName());
        }
        return 0;
    }
}

class ScriptTask extends Task {
    CustomizerBase cb;
    String script;
    List<String> scripts;
    Player player;
    Event event;
    Object data;

    public HashMap<String, Object> localvars = new HashMap<>();

    public ScriptTask(CustomizerBase cb, Player player, ScriptBase script, Event event, Object data) {
        this.cb = cb;
        this.player = player;
        this.script = script.getName();
        this.scripts = script.getLines();
        this.event = event;
        this.data = data;

    }

    public void r() {
        if (isLong(CustomizerVariable.getPDFS(player.getName(), "cooldowns." + script)))
            if (Long.parseLong(CustomizerVariable.getPDFS(player.getName(), "cooldowns." + script)) > (new Date()).getTime()) {
                player.sendMessage("COOLDOWN");
                return;
            }

        Server.getInstance().getScheduler().scheduleTask(this);
    }

    int idx = 0;

    @Override
    public void onRun(int currentTick) {
        while (idx < scripts.size()) {
            String source = scripts.get(idx);

            source = CustomizerExecutor.parsePlaceHolder(cb, player, source, event, data);

            int i = CustomizerExecutor.runFunction(cb, this, script, player, event, source);

            if (i == -1)
                break;

            if (i > 0) {
                Server.getInstance().getScheduler().scheduleDelayedTask(this, i);
                break;
            }

            idx++;
        }
    }

    private boolean isLong(String num) {
        try {
            Long.parseLong(num);
            return true;
        } catch (Exception err) {
        }
        return false;
    }

}
