package iKguana.customizer.commands;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import iKguana.artonline.SimpleDialog.SimpleDialog;
import iKguana.customizer.interfaces.ScriptBase;
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

public class CustomizerScript extends CustomizerBase {


    public CustomizerScript() {
        setName("CustomizerScript");

        new ScriptManager(new File(Customizer.getInstance().getDataFolder() + File.separator + "Scripts"));

        CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));

        CustomizerExecutor.customGlobalFunctions.put("@RUNSCRIPT", this);
    }

    @Override
    public void executeCustomGlobalFunction(CustomizerBase cb, Player player, Event event, String line, String fc, String arg, String[] args) {
        if (fc.equals("@RUNSCRIPT")) {
            if (args.length >= 1)
                if (args.length >= 2)
                    CustomizerExecutor.executeScript(cb, player, CustomizerScript.getScript(args[0]), event, null, args[1]);
                else
                    CustomizerExecutor.executeScript(cb, player, CustomizerScript.getScript(args[0]), event, null);
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
                window.addButton(new ElementButton("스크립트 리로드"));
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
            SimpleDialog.sendDialog(this, "form_showScript", event.getPlayer(), SimpleDialog.Type.FILTERING, ScriptManager.getIt().getAllScript());
        } else if (text.equals("스크립트 편집")) {
            SimpleDialog.sendDialog(this, "form_editScript", event.getPlayer(), SimpleDialog.Type.FILTERING, ScriptManager.getIt().getAllScript());
        } else if (text.equals("스크립트 삭제")) {
            SimpleDialog.sendDialog(this, "form_removeScript", event.getPlayer(), SimpleDialog.Type.FILTERING, ScriptManager.getIt().getAllScript());
        }
    }

    public void form_addSource(PlayerFormRespondedEvent event, Object data) {
        FormResponseCustom response = (FormResponseCustom) event.getResponse();
        String name = response.getInputResponse(0);
        CScript script = ScriptManager.getIt().getCScript(name);
        String source_0 = response.getInputResponse(1);
        String source_1 = response.getInputResponse(2);

        if (name.trim().length() != 0) {
            script.addLine(source_0);
            if (source_1.trim().length() != 0)
                script.addLine(source_1);
            script.save();

            SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "소스가 정상적으로 추가되었습니다.");
        } else
            SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "스크립트의 이름은 공백이 될수없습니다.");
    }

    public void form_showScript(PlayerFormRespondedEvent event, Object data) {
        String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();

        CScript script = ScriptManager.getIt().getCScript(name);

        SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, script.getLinesString());
    }

    public void form_editScript(PlayerFormRespondedEvent event, Object data) {
        String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();

        CScript script = ScriptManager.getIt().getCScript(name);

        FormWindowCustom window = new FormWindowCustom("스크립트 수정");
        for (String source : script.getLines())
            window.addElement(new ElementInput("", "", source));

        SimpleDialog.sendDialog(this, "form_applySource", event.getPlayer(), window, name);
    }

    public void form_applySource(PlayerFormRespondedEvent event, Object data) {
        FormResponseCustom response = (FormResponseCustom) event.getResponse();

        CScript script = ScriptManager.getIt().getCScript((String) data);

        for (int i = 0; i < response.getResponses().keySet().size(); i++)
            script.replaceLine(i, response.getInputResponse(i));
        script.save();

        SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "수정되었습니다.");
    }

    public void form_removeScript(PlayerFormRespondedEvent event, Object data) {
        String name = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();

        ScriptManager.getIt().removeCScript(name);

        SimpleDialog.sendDialog(this, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "스크립트가 제거되었습니다.");
    }

    public static CScript getScript(String name) {
        return ScriptManager.getIt().getCScript(name);
    }
}

class ScriptManager {
    private static ScriptManager $instance;
    File SCRIPTS_DIR;
    String format = ".is";

    public ScriptManager(File SCRIPTS_DIR) {
        $instance = this;
        this.SCRIPTS_DIR = SCRIPTS_DIR;
        this.SCRIPTS_DIR.mkdirs();
    }

    HashMap<String, CScript> scripts = new HashMap<>();

    public CScript getCScript(String script) {
        if (!scripts.containsKey(script))
            scripts.put(script, new CScript(script, new File(SCRIPTS_DIR, script + format)));

        return scripts.get(script);
    }

    public void removeCScript(String script) {
        getCScript(script).delete();
        scripts.remove(script);
    }

    public ArrayList<String> getAllScript() {
        ArrayList<String> labels = new ArrayList<>();
        for (File script : SCRIPTS_DIR.listFiles())
            if (script.getName().endsWith(format))
                labels.add(script.getName().substring(0, (script.getName().length() - format.length())));

        return labels;
    }

    public void reload() {
        scripts = new HashMap<>();
    }

    public static ScriptManager getIt() {
        return $instance;
    }
}

class CScript extends ScriptBase {
    String name;
    File file;
    ArrayList<String> sources = new ArrayList<>();

    public CScript(String name, File file) {
        super(name);
        this.file = file;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while ((line = reader.readLine()) != null)
                addLine(line);

            reader.close();
        } catch (Exception err) {
        }
    }

    @Override
    public void addLine(String line) {
        sources.add(line);
    }

    @Override
    public void replaceLine(int idx, String line) {
        sources.set(idx, line);
    }

    @Override
    public void deleteLine(int idx) {
        sources.remove(idx);
    }

    @Override
    public List<String> getLines() {
        return sources;
    }

    @Override
    public String getLinesString() {
        String str = "";
        for (String source : sources)
            str += source + "\n";

        return str.trim();
    }

    @Override
    public void delete() {
        file.delete();
    }

    @Override
    public void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(getLinesString());
            bw.flush();
            bw.close();
        } catch (Exception err) {
            Server.getInstance().getLogger().error("스크립트 저장에 실패하였습니다. (" + name + ") [" + err.getClass().getSimpleName() + "]");
        }
    }
}

