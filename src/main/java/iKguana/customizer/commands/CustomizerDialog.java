package iKguana.customizer.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Event;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementStepSlider;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.Config;
import iKguana.artonline.SimpleDialog.SimpleDialog;
import iKguana.customizer.Customizer;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.CustomizerExecutor;
import iKguana.customizer.commands.DialogWindowCustom.E_Type;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;

public class CustomizerDialog extends CustomizerBase {
    public static File DIALOG_FOLDER;

    public CustomizerDialog() {
        setName("CustomizerDialog");

        DIALOG_FOLDER = new File(Customizer.getInstance().getDataFolder() + File.separator + "Dialogs");
        DIALOG_FOLDER.mkdirs();

        new DialogManager();

        CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));

        CustomizerExecutor.customGlobalFunctions.put("@SENDDIALOG", this);
    }

    @Override
    public String getCustomPlaceHolder(String tag, Object data) {
        if (tag.equals("result")) {
            if (data instanceof Object[]) {
                PlayerFormRespondedEvent event = (PlayerFormRespondedEvent) ((Object[]) data)[0];

                if (event.getResponse() instanceof FormResponseModal) {
                    FormResponseModal response = (FormResponseModal) event.getResponse();

                    if (response.getClickedButtonId() == 0)
                        return String.valueOf(true);
                    else
                        return String.valueOf(false);
                } else if (event.getResponse() instanceof FormResponseSimple) {
                    FormResponseSimple response = (FormResponseSimple) event.getResponse();

                    return response.getClickedButton().getText();
                }
            }
        }
        return super.getCustomPlaceHolder(tag, data);
    }

    @Override
    public String getCustomPlaceHolder(String tag, String arg, Object data) {
        if (tag.equals("result")) {
            @SuppressWarnings("unchecked")
            HashMap<Integer, Object> responses = (HashMap<Integer, Object>) data;
            if (isInteger(arg)) {
                if (responses.containsKey(Integer.parseInt(arg)))
                    return String.valueOf(responses.get(Integer.parseInt(arg)));
            }
        }
        return super.getCustomPlaceHolder(tag, arg, data);
    }

    @Override
    public void executeCustomGlobalFunction(CustomizerBase cb, Player player, Event event, String line, String fc, String arg, String[] args) {
        switch (fc) {
            case "@SENDDIALOG":
                if (args.length >= 2) {
                    if (isPlayerOnline(args[0])) {
                        String[] strings = toArray(arg.substring(arg.indexOf(args[1]) + args[1].length(), arg.length()).trim());
                        SimpleDialog.sendDialog(this, "customizer_dialog_receiver", player, DialogManager.getIt().getDialog(args[1]).getForm(getPlayerOnline(args[0]), strings), DialogManager.getIt().getDialog(args[1]));
                    } else
                        SimpleDialog.sendDialog(null, null, getPlayerOnline(args[0]), SimpleDialog.Type.ONLY_TEXT, "플레이어가 존재하지않습니다.");
                } else
                    SimpleDialog.sendDialog(null, null, getPlayerOnline(args[0]), SimpleDialog.Type.ONLY_TEXT, "인자부족.");
                break;
        }
    }

    public String[] toArray(String text) {
        ArrayList<String> list = new ArrayList<>();
        String str = "";
        for (char c : text.toCharArray()) {
            if (c == '$') {
                list.add(str);
                str = "";
            } else
                str += c;
        }
        list.add(str);

        return list.toArray(new String[]{});
    }

    public void customizer_dialog_receiver(PlayerFormRespondedEvent event, Object data) {
        if (event.getWindow() instanceof FormWindowModal) {
            DialogWindowModal dialog = (DialogWindowModal) data;
            FormResponseModal response = (FormResponseModal) event.getResponse();

            String script = dialog.getScript();

            CustomizerExecutor.executeScript(this, event.getPlayer(), CustomizerScript.getScript(script), event, new Object[]{event, data});
        } else if (event.getWindow() instanceof FormWindowSimple) {
            DialogWindowSimple dialog = (DialogWindowSimple) data;
            FormResponseSimple response = (FormResponseSimple) event.getResponse();

            CustomizerExecutor.executeScript(this, event.getPlayer(), CustomizerScript.getScript(dialog.getScript()), event, new Object[]{event, data});
        } else if (event.getWindow() instanceof FormWindowCustom) {
            DialogWindowCustom dialog = (DialogWindowCustom) data;
            FormResponseCustom response = (FormResponseCustom) event.getResponse();

            CustomizerExecutor.executeScript(this, event.getPlayer(), CustomizerScript.getScript(dialog.getScript()), event, response.getResponses());
        }
    }

    ////////////////////////////////////////
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals(CT.getMessage(getName(), "label"))) {
            if (sender.isPlayer()) {
                FormWindowSimple window = new FormWindowSimple(getName(), "사용할 기능을 클릭해주세요.");
                window.addButton(new ElementButton("추가"));
                window.addButton(new ElementButton("미리보기"));
                window.addButton(new ElementButton("편집"));
                window.addButton(new ElementButton("삭제"));
                SimpleDialog.sendDialog(this, "form_dialog_menu", (Player) sender, window);
            } else
                sender.sendMessage(CT.getMessage("__Public", "NOT_IN_GAME"));
            return true;
        }
        return false;
    }

    public void form_dialog_menu(PlayerFormRespondedEvent event, Object data) {
        String text = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        if (text.equals("추가")) {
            FormWindowCustom window = new FormWindowCustom("다이어로그 추가");
            window.addElement(new ElementInput("다이어로그의 이름을 입력해주세요."));

            SimpleDialog.sendDialog(this, "form_dialog_in", event.getPlayer(), window);
        } else if (text.equals("미리보기")) {
            SimpleDialog.sendDialog(this, "form_preview_dialog", event.getPlayer(), SimpleDialog.Type.FILTERING, DialogManager.getIt().getAllDialogNames());
        } else if (text.equals("편집")) {
            SimpleDialog.sendDialog(this, "form_edit_dialog", event.getPlayer(), SimpleDialog.Type.FILTERING, DialogManager.getIt().getAllDialogNames());
        } else if (text.equals("삭제")) {
            SimpleDialog.sendDialog(this, "form_delete_dialog", event.getPlayer(), SimpleDialog.Type.FILTERING, DialogManager.getIt().getAllDialogNames());
        }
    }

    public void form_dialog_in(PlayerFormRespondedEvent event, Object data) {
        String name = ((FormResponseCustom) event.getResponse()).getInputResponse(0);
        if (!DialogManager.isDialogExist(name)) {// 다이어로그 생성
            FormWindowCustom window = new FormWindowCustom("다이어로그 추가");
            window.addElement(new ElementDropdown("다이어로그 종류", D_Type.getTypes()));

            SimpleDialog.sendDialog(this, "form_dialog_st_1", event.getPlayer(), window, name);
        } else {
            if (DialogManager.getIt().getType(name) == D_Type.FormWindowModal) {
                DialogWindowModal dialog = (DialogWindowModal) DialogManager.getIt().getDialog(name);

                FormWindowCustom window = new FormWindowCustom("WindowModal");
                window.addElement(new ElementInput("타이틀", "", dialog.getTitle()));
                window.addElement(new ElementInput("설명", "", dialog.getContext()));
                window.addElement(new ElementInput("스크립트", "", dialog.getScript()));
                window.addElement(new ElementInput("1번버튼 텍스트", "", dialog.getAText()));
                window.addElement(new ElementInput("2번버튼 텍스트", "", dialog.getBText()));

                SimpleDialog.sendDialog(this, "form_dialog_set_modal", event.getPlayer(), window, name);
            } else if (DialogManager.getIt().getType(name) == D_Type.FormWindowSimple) {
                DialogWindowSimple dialog = (DialogWindowSimple) DialogManager.getIt().getDialog(name);

                FormWindowCustom window = new FormWindowCustom("ADDBUTTON");
                window.addElement(new ElementLabel(name));
                window.addElement(new ElementInput("버튼 텍스트"));
                window.addElement(new ElementSlider("색인", 0, dialog.getRawButtons().size(), 1, dialog.getRawButtons().size()));

                SimpleDialog.sendDialog(this, "form_dialog_addbutton_simple", event.getPlayer(), window, dialog);
            } else if (DialogManager.getIt().getType(name) == D_Type.FormWindowCustom) {
                DialogWindowCustom dialog = (DialogWindowCustom) DialogManager.getIt().getDialog(name);

                FormWindowCustom window = new FormWindowCustom("CUSTOM_ADDELEMENT");
                window.addElement(new ElementLabel("DIALOG : " + dialog.getDName()));
                window.addElement(new ElementDropdown("종류를 선택해주세요.", DialogWindowCustom.getElementTypesString()));

                SimpleDialog.sendDialog(this, "form_dialog_elementtype_custom", event.getPlayer(), window, dialog);
            }
        }
    }

    HashMap<String, String> savedDialogName = new HashMap<>();

    public void form_dialog_st_1(PlayerFormRespondedEvent event, Object data) {
        String name = (String) data;
        String type = ((FormResponseCustom) event.getResponse()).getDropdownResponse(0).getElementContent();

        if (D_Type.get(type) == D_Type.FormWindowModal) {
            FormWindowCustom window = new FormWindowCustom("WindowModal");
            window.addElement(new ElementInput("타이틀"));
            window.addElement(new ElementInput("설명"));
            window.addElement(new ElementInput("스크립트"));
            window.addElement(new ElementInput("1번버튼 텍스트"));
            window.addElement(new ElementInput("2번버튼 텍스트"));

            SimpleDialog.sendDialog(this, "form_dialog_set_modal", event.getPlayer(), window, name);
        } else if (D_Type.get(type) == D_Type.FormWindowSimple) {
            FormWindowCustom window = new FormWindowCustom("WindowSimple");
            window.addElement(new ElementInput("타이틀"));
            window.addElement(new ElementInput("설명"));
            window.addElement(new ElementInput("스크립트"));
            SimpleDialog.sendDialog(this, "form_dialog_setdefault_simple", event.getPlayer(), window, name);
        } else if (D_Type.get(type) == D_Type.FormWindowCustom) {
            FormWindowCustom window = new FormWindowCustom("WindowCustom");
            window.addElement(new ElementInput("타이틀"));
            window.addElement(new ElementInput("스크립트"));
            SimpleDialog.sendDialog(this, "form_dialog_setdefault_custom", event.getPlayer(), window, name);
        } else
            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "오류가 발생하였습니다.\n(UNKNOWN_DIALOG_TYPE))");
    }

    public void form_dialog_set_modal(PlayerFormRespondedEvent event, Object data) {
        String title = ((FormResponseCustom) event.getResponse()).getInputResponse(0);
        String context = ((FormResponseCustom) event.getResponse()).getInputResponse(1);
        String script = ((FormResponseCustom) event.getResponse()).getInputResponse(2);
        String at = ((FormResponseCustom) event.getResponse()).getInputResponse(3);
        String bt = ((FormResponseCustom) event.getResponse()).getInputResponse(4);

        DialogWindowModal dialog = (DialogWindowModal) DialogManager.getIt().getDialog((String) data, D_Type.FormWindowModal);
        dialog.setTitle(title);
        dialog.setContext(context);
        dialog.setScript(script);
        dialog.setAText(at);
        dialog.setBText(bt);
        dialog.save();

        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 저장되었습니다.");
    }

    public void form_dialog_setdefault_simple(PlayerFormRespondedEvent event, Object data) {
        String title = ((FormResponseCustom) event.getResponse()).getInputResponse(0);
        String context = ((FormResponseCustom) event.getResponse()).getInputResponse(1);
        String script = ((FormResponseCustom) event.getResponse()).getInputResponse(2);

        DialogWindowSimple dialog = (DialogWindowSimple) DialogManager.getIt().getDialog((String) data, D_Type.FormWindowSimple);
        dialog.setTitle(title);
        dialog.setContext(context);
        dialog.setScript(script);

        FormWindowCustom window = new FormWindowCustom("SIMPLE_ADDBUTTON");
        window.addElement(new ElementLabel("DIALOG : " + (String) data));
        window.addElement(new ElementInput("버튼 텍스트"));
        window.addElement(new ElementSlider("색인", 0, dialog.getRawButtons().size(), 1, dialog.getRawButtons().size()));

        SimpleDialog.sendDialog(this, "form_dialog_addbutton_simple", event.getPlayer(), window, dialog);
    }

    public void form_dialog_addbutton_simple(PlayerFormRespondedEvent event, Object data) {
        DialogWindowSimple dialog = (DialogWindowSimple) data;
        String btn_text = ((FormResponseCustom) event.getResponse()).getInputResponse(1);
        int idx = (int) ((FormResponseCustom) event.getResponse()).getSliderResponse(2);

        dialog.addButton(btn_text, idx);
        dialog.save();

        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 저장되었습니다.");
    }

    public void form_dialog_setdefault_custom(PlayerFormRespondedEvent event, Object data) {
        String title = ((FormResponseCustom) event.getResponse()).getInputResponse(0);
        String script = ((FormResponseCustom) event.getResponse()).getInputResponse(1);

        DialogWindowCustom dialog = (DialogWindowCustom) DialogManager.getIt().getDialog((String) data, D_Type.FormWindowCustom);
        dialog.setTitle(title);
        dialog.setScript(script);

        FormWindowCustom window = new FormWindowCustom("CUSTOM_ADDELEMENT");
        window.addElement(new ElementLabel("DIALOG : " + (String) data));
        window.addElement(new ElementDropdown("종류를 선택해주세요.", DialogWindowCustom.getElementTypesString()));

        SimpleDialog.sendDialog(this, "form_dialog_elementtype_custom", event.getPlayer(), window, dialog);
    }

    public void form_dialog_elementtype_custom(PlayerFormRespondedEvent event, Object data) {
        DialogWindowCustom dialog = (DialogWindowCustom) data;
        String etype = ((FormResponseCustom) event.getResponse()).getDropdownResponse(1).getElementContent();

        if (E_Type.get(etype) == E_Type.ElementLabel) {
            FormWindowCustom window = new FormWindowCustom(etype);
            window.addElement(new ElementInput("text"));
            window.addElement(new ElementSlider("색인", 0, dialog.getRawElements().size(), 1, dialog.getRawElements().size()));

            SimpleDialog.sendDialog(this, "form_dialog_addelement_custom", event.getPlayer(), window, dialog);
        } else if (E_Type.get(etype) == E_Type.ElementInput) {
            FormWindowCustom window = new FormWindowCustom(etype);
            window.addElement(new ElementInput("text", "String"));
            window.addElement(new ElementInput("placeholder", "String"));
            window.addElement(new ElementInput("defaulttext", "String"));
            window.addElement(new ElementSlider("색인", 0, dialog.getRawElements().size(), 1, dialog.getRawElements().size()));

            SimpleDialog.sendDialog(this, "form_dialog_addelement_custom", event.getPlayer(), window, dialog);
        } else if (E_Type.get(etype) == E_Type.ElementToggle) {
            FormWindowCustom window = new FormWindowCustom(etype);
            window.addElement(new ElementInput("text", "String"));
            window.addElement(new ElementSlider("색인", 0, dialog.getRawElements().size(), 1, dialog.getRawElements().size()));

            SimpleDialog.sendDialog(this, "form_dialog_addelement_custom", event.getPlayer(), window, dialog);
        } else if (E_Type.get(etype) == E_Type.ElementSlider) {
            FormWindowCustom window = new FormWindowCustom(etype);
            window.addElement(new ElementInput("text", "String"));
            window.addElement(new ElementInput("min", "float"));
            window.addElement(new ElementInput("max", "float"));
            window.addElement(new ElementInput("step", "int"));
            window.addElement(new ElementInput("defaultValue", "float"));
            window.addElement(new ElementSlider("색인", 0, dialog.getRawElements().size(), 1, dialog.getRawElements().size()));

            SimpleDialog.sendDialog(this, "form_dialog_addelement_custom", event.getPlayer(), window, dialog);
        } else if (E_Type.get(etype) == E_Type.ElementStepSlider) {
            FormWindowCustom window = new FormWindowCustom(etype);
            window.addElement(new ElementInput("text", "String"));
            window.addElement(new ElementInput("steps", "Array : (ex: str1$str2$str3)"));
            window.addElement(new ElementInput("defaultstep", "int"));

            SimpleDialog.sendDialog(this, "form_dialog_addelement_custom", event.getPlayer(), window, dialog);
        } else if (E_Type.get(etype) == E_Type.ElementDropdown) {
            FormWindowCustom window = new FormWindowCustom(etype);
            window.addElement(new ElementInput("text", "String"));
            window.addElement(new ElementInput("options", "Array : (ex: str1$str2$str3)"));
            window.addElement(new ElementInput("defaultOption", "int"));

            SimpleDialog.sendDialog(this, "form_dialog_addelement_custom", event.getPlayer(), window, dialog);
        }
    }

    public void form_dialog_addelement_custom(PlayerFormRespondedEvent event, Object data) {
        FormWindowCustom window = (FormWindowCustom) event.getWindow();
        FormResponseCustom response = (FormResponseCustom) event.getResponse();
        DialogWindowCustom dialog = (DialogWindowCustom) data;
        E_Type type = E_Type.get(window.getTitle());

        if (type == E_Type.ElementLabel) {
            dialog.addElement(new ElementLabel(response.getInputResponse(0)), (int) response.getSliderResponse(1));
        } else if (type == E_Type.ElementInput) {
            dialog.addElement(new ElementInput(response.getInputResponse(0), response.getInputResponse(1), response.getInputResponse(2)), (int) response.getSliderResponse(3));
        } else if (type == E_Type.ElementToggle) {
            dialog.addElement(new ElementToggle(response.getInputResponse(0)), (int) response.getSliderResponse(1));
        } else if (type == E_Type.ElementSlider) {
            if (isFloat(response.getInputResponse(1), response.getInputResponse(2), response.getInputResponse(4)) && isInteger(response.getInputResponse(3)))
                dialog.addElement(new ElementSlider(response.getInputResponse(0), Float.parseFloat(response.getInputResponse(1)), Float.parseFloat(response.getInputResponse(2)), Integer.parseInt(response.getInputResponse(3)), Float.parseFloat(response.getInputResponse(4))));
            else {
                SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "올바르지않은 데이터가 존재합니다.");
                return;
            }
        } else if (type == E_Type.ElementStepSlider) {
            if (isInteger(response.getInputResponse(2)))
                dialog.addElement(new ElementStepSlider(response.getInputResponse(0), new ArrayList<String>(Arrays.asList(toArray(response.getInputResponse(1)))), Integer.parseInt(response.getInputResponse(2))));
            else {
                SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "올바르지않은 데이터가 존재합니다.");
                return;
            }
        } else if (type == E_Type.ElementDropdown) {
            if (isInteger(response.getInputResponse(2)))
                dialog.addElement(new ElementDropdown(response.getInputResponse(0), new ArrayList<String>(Arrays.asList(toArray(response.getInputResponse(1)))), Integer.parseInt(response.getInputResponse(2))));
            else {
                SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "올바르지 않은 데이터가 존재합니다.");
                return;
            }
        }

        dialog.save();
        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 저장되었습니다.");
    }

    public void form_preview_dialog(PlayerFormRespondedEvent event, Object data) {
        String dialogname = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();

        SimpleDialog.sendDialog(null, null, event.getPlayer(), DialogManager.getIt().getDialog(dialogname).getForm(event.getPlayer()));
    }

    public void form_edit_dialog(PlayerFormRespondedEvent event, Object data) {
        String dialogname = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        if (DialogManager.getIt().getDialog(dialogname).getType() == D_Type.FormWindowModal) {
            DialogWindowModal dialog = (DialogWindowModal) DialogManager.getIt().getDialog(dialogname);

            FormWindowCustom window = new FormWindowCustom("EDIT_WindowModal");
            window.addElement(new ElementInput("타이틀", "", dialog.getTitle()));
            window.addElement(new ElementInput("설명", "", dialog.getContext()));
            window.addElement(new ElementInput("스크립트", "", dialog.getScript()));
            window.addElement(new ElementInput("1번버튼 텍스트", "", dialog.getAText()));
            window.addElement(new ElementInput("2번버튼 텍스트", "", dialog.getBText()));
            SimpleDialog.sendDialog(this, "form_dialog_set_modal", event.getPlayer(), window, dialogname);
        } else if (DialogManager.getIt().getDialog(dialogname).getType() == D_Type.FormWindowSimple) {
            DialogWindowSimple dialog = (DialogWindowSimple) DialogManager.getIt().getDialog(dialogname);

            FormWindowSimple window = new FormWindowSimple("EDIT_WindowSimple", "편집하거나 삭제할 요소를 클릭해주세요.\n타이틀/설명/스크립트 : 편집\n나머지 요소 : 삭제");
            window.addButton(new ElementButton("타이틀/설명/스크립트"));
            for (ElementButton btn : dialog.getButtons())
                window.addButton(btn);

            SimpleDialog.sendDialog(this, "form_dialog_edit_simple", event.getPlayer(), window, dialog);
        } else if (DialogManager.getIt().getDialog(dialogname).getType() == D_Type.FormWindowCustom) {
            DialogWindowCustom dialog = (DialogWindowCustom) DialogManager.getIt().getDialog(dialogname);

            FormWindowSimple window = new FormWindowSimple("EDIT_WindowCustom", "편집하거나 삭제할 요소를 클릭해주세요.\n타이틀/설명/스크립트 : 편집\n나머지 요소 : 삭제");
            window.addButton(new ElementButton("타이틀/스크립트"));
            for (Map<Object, Object> raw : dialog.getRawElements())
                window.addButton(new ElementButton((String) raw.get("ElementType")));

            SimpleDialog.sendDialog(this, "form_dialog_edit_custom", event.getPlayer(), window, dialog);
        }
    }

    public void form_dialog_edit_simple(PlayerFormRespondedEvent event, Object data) {
        FormResponseSimple response = (FormResponseSimple) event.getResponse();
        DialogWindowSimple dialog = (DialogWindowSimple) data;

        if (response.getClickedButtonId() == 0) {
            FormWindowCustom window = new FormWindowCustom("EDIT_CUSTOM_ELEMENTS");
            window.addElement(new ElementInput("타이틀", dialog.getTitle(), dialog.getTitle()));
            window.addElement(new ElementInput("설명", dialog.getContext(), dialog.getContext()));
            window.addElement(new ElementInput("스크립트", dialog.getScript(), dialog.getScript()));

            SimpleDialog.sendDialog(this, "form_edit_elements", event.getPlayer(), window, data);
        } else {
            dialog.removeButton(response.getClickedButtonId() - 1);
            dialog.save();
            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 저장되었습니다.");
        }
    }

    public void form_dialog_edit_custom(PlayerFormRespondedEvent event, Object data) {
        FormResponseSimple response = (FormResponseSimple) event.getResponse();
        DialogWindowCustom dialog = (DialogWindowCustom) data;

        if (response.getClickedButtonId() == 0) {
            FormWindowCustom window = new FormWindowCustom("EDIT_CUSTOM_ELEMENTS");
            window.addElement(new ElementInput("타이틀", dialog.getTitle(), dialog.getTitle()));
            window.addElement(new ElementInput("스크립트", dialog.getScript(), dialog.getScript()));

            SimpleDialog.sendDialog(this, "form_edit_elements", event.getPlayer(), window, data);
        } else {
            dialog.removeElement(response.getClickedButtonId() - 1);
            dialog.save();
            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 저장되었습니다.");
        }
    }

    public void form_edit_elements(PlayerFormRespondedEvent event, Object data) {
        FormResponseCustom response = (FormResponseCustom) event.getResponse();
        if (data instanceof DialogWindowSimple) {
            DialogWindowSimple dialog = (DialogWindowSimple) data;
            dialog.setTitle(response.getInputResponse(0));
            dialog.setContext(response.getInputResponse(1));
            dialog.setScript(response.getInputResponse(2));
            dialog.save();
            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 저장되었습니다.");
        } else if (data instanceof DialogWindowCustom) {
            DialogWindowCustom dialog = (DialogWindowCustom) data;
            dialog.setTitle(response.getInputResponse(0));
            dialog.setScript(response.getInputResponse(1));
            dialog.save();
            SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 저장되었습니다.");
        } else
            sendError(event.getPlayer(), "UNKNOWN_DIALOG_TYPE");
    }


    public void form_delete_dialog(PlayerFormRespondedEvent event, Object data) {
        String dialogname = ((FormResponseSimple) event.getResponse()).getClickedButton().getText();
        DialogManager.getIt().removeDialog(dialogname);

        SimpleDialog.sendDialog(null, null, event.getPlayer(), SimpleDialog.Type.ONLY_TEXT, "다이어로그가 삭제되었습니다.");
    }
}

enum D_Type {
    FormWindowSimple("FormWindowSimple"), FormWindowCustom("FormWindowCustom"), FormWindowModal("FormWindowModal");

    String t;

    private D_Type(String t) {
        this.t = t;
    }

    public String getString() {
        return t;
    }

    public static D_Type get(String text) {
        for (D_Type ty : D_Type.values())
            if (ty.getString().equalsIgnoreCase(text))
                return ty;
        return null;
    }

    public static ArrayList<String> getTypes() {
        ArrayList<String> list = new ArrayList<>();
        for (D_Type type : D_Type.values())
            list.add(type.getString());
        return list;
    }
}

class DialogManager {
    private static DialogManager $instance;

    public DialogManager() {
        $instance = this;
    }

    public static File getDialogFile(String name) {
        return new File(CustomizerDialog.DIALOG_FOLDER + File.separator + name + ".json");
    }

    public static boolean isDialogExist(String name) {
        return getDialogFile(name).isFile();
    }

    public ArrayList<String> getAllDialogNames() {
        ArrayList<String> list = new ArrayList<>();

        for (File dialog : CustomizerDialog.DIALOG_FOLDER.listFiles())
            if (dialog.isFile() && dialog.getName().endsWith(".json"))
                list.add(dialog.getName().replace(".json", ""));

        return list;
    }

    public D_Type getType(String name) {
        if (isDialogExist(name)) {
            Config cfg = new Config(CustomizerDialog.DIALOG_FOLDER + File.separator + name + ".json", Config.JSON);
            return D_Type.get(cfg.getString("DialogType"));
        } else
            return null;
    }

    public HashMap<String, DialogWindow> dialogs = new HashMap<>();

    public DialogWindow getDialog(String name) {
        return getDialog(name, getType(name));
    }

    public DialogWindow getDialog(String name, D_Type type) {
        if (!dialogs.containsKey(name)) {
            if (type == D_Type.FormWindowModal)
                dialogs.put(name, new DialogWindowModal(name));
            else if (type == D_Type.FormWindowSimple)
                dialogs.put(name, new DialogWindowSimple(name));
            else if (type == D_Type.FormWindowCustom)
                dialogs.put(name, new DialogWindowCustom(name));
            else
                return null;
        }
        return dialogs.get(name);
    }

    public void removeDialog(String name) {
        if (dialogs.containsKey(name)) {
            dialogs.get(name).delete();
            dialogs.remove(name);
        }
    }

    public static DialogManager getIt() {
        return $instance;
    }
}

abstract class DialogWindow {
    private final File DIALOG_FILE;
    Config cfg = null;

    private final String NAME;
    private D_Type type = null;
    private String title = "";

    public DialogWindow(String name, D_Type type) {
        NAME = name;
        DIALOG_FILE = new File(CustomizerDialog.DIALOG_FOLDER + File.separator + name + ".json");

        setType(type);

        init();
    }

    public void init() {
        if (DialogManager.isDialogExist(getDName()))
            initConfig();
    }

    public File getDFile() {
        return DIALOG_FILE;
    }

    public String getDName() {
        return NAME;
    }

    public void setType(D_Type t) {
        type = t;
    }

    public D_Type getType() {
        return type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void delete() {
        DIALOG_FILE.delete();
    }

    protected static String replaceAll(String str, String[] args) {
        for (int i = 0; i < args.length; i++)
            str = str.replace("%" + (i + 1), args[i]);
        return str;
    }

    public void initConfig() {
        if (cfg == null)
            cfg = new Config(getDFile(), Config.JSON);
    }

    public void save() {
        initConfig();

        cfg.set("DialogType", getType().getString());
    }

    public FormWindow getForm(Player player) {
        return getForm(player, new String[]{});
    }

    public FormWindow getForm(Player player, String[] strings) {
        return null;
    }
}

class DialogWindowModal extends DialogWindow {
    private String context = "";
    private String script = "";
    private String AText = "";
    private String BText = "";

    public DialogWindowModal(String name) {
        super(name, D_Type.FormWindowModal);

        if (cfg != null) {
            setTitle(cfg.getString("Title"));
            setContext(cfg.getString("Context"));
            setScript(cfg.getString("Script"));
            setAText(cfg.getString("A_Text"));
            setBText(cfg.getString("B_Text"));
        }
    }

    public void setContext(String ctx) {
        context = ctx;
    }

    public String getContext() {
        return context;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setAText(String text) {
        AText = text;
    }

    public String getAText() {
        return AText;
    }

    public void setBText(String text) {
        BText = text;
    }

    public String getBText() {
        return BText;
    }

    @Override
    public void save() {
        super.save();

        cfg.set("Title", getTitle());
        cfg.set("Context", getContext());
        cfg.set("Script", getContext());
        cfg.set("A_Text", getAText());
        cfg.set("B_Text", getBText());

        cfg.save();
    }

    @Override
    public FormWindow getForm(Player player, String[] strings) {
        FormWindowModal window = new FormWindowModal(replaceAll(getTitle(), strings), replaceAll(getContext(), strings), replaceAll(getAText(), strings), replaceAll(getBText(), strings));
        return window;
    }
}

class DialogWindowSimple extends DialogWindow {
    private String context = "";
    private String script = "";

    public DialogWindowSimple(String name) {
        super(name, D_Type.FormWindowSimple);

        if (cfg != null) {
            setTitle(cfg.getString("Title"));
            setContext(cfg.getString("Context"));
            setScript(cfg.getString("Script"));
            @SuppressWarnings("unchecked")
            ArrayList<Object> list = (ArrayList<Object>) cfg.getList("Buttons");
            for (Object obj : list) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) obj;
            }
        }
    }

    public void setContext(String ctx) {
        context = ctx;
    }

    public String getContext() {
        return context;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void addButton(String text) {
        addButton(text, btns.size());
    }

    public void addButton(String text, int idx) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("text", text);

        btns.add(idx, map);
    }

    public ElementButton getButton(int idx) {
        return new ElementButton(btns.get(idx).get("text"));
    }

    public void removeButton(int idx) {
        btns.remove(idx);
    }

    public ArrayList<ElementButton> getButtons() {
        ArrayList<ElementButton> btns_ = new ArrayList<>();
        for (LinkedHashMap<String, String> map : getRawButtons())
            btns_.add(new ElementButton(map.get("text")));

        return btns_;
    }

    ArrayList<LinkedHashMap<String, String>> btns = new ArrayList<>();

    public ArrayList<LinkedHashMap<String, String>> getRawButtons() {
        return btns;
    }

    @Override
    public void save() {
        super.save();

        cfg.set("Title", getTitle());
        cfg.set("Context", getContext());
        cfg.set("Script", getScript());
        cfg.set("Buttons", getRawButtons());

        cfg.save();
    }

    public ElementButton convert(ElementButton btn, Player player, String[] s) {
        btn.setText(convert(btn.getText(), player, s));
        return btn;
    }

    public String convert(String text, Player player, String[] s) {
        text = replaceAll(text, s);
        text = CustomizerExecutor.parsePlaceHolder(null, player, text, null, null);
        return text;
    }

    @Override
    public FormWindow getForm(Player player, String[] s) {
        FormWindowSimple window = new FormWindowSimple(convert(getTitle(), player, s), convert(getContext(), player, s));
        for (ElementButton btn : getButtons())
            window.addButton(convert(btn, player, s));

        return window;
    }
}

class DialogWindowCustom extends DialogWindow {
    enum E_Type {
        ElementInput(ElementInput.class.getSimpleName()), // Prevent eclipse code format
        ElementLabel(ElementLabel.class.getSimpleName()), //
        ElementDropdown(ElementDropdown.class.getSimpleName()), //
        ElementSlider(ElementSlider.class.getSimpleName()), //
        ElementStepSlider(ElementStepSlider.class.getSimpleName()), //
        ElementToggle(ElementToggle.class.getSimpleName());

        String t;

        private E_Type(String t) {
            this.t = t;
        }

        public String getString() {
            return t;
        }

        public static E_Type get(String text) {
            for (E_Type ty : E_Type.values())
                if (ty.getString().equalsIgnoreCase(text))
                    return ty;
            return null;
        }

        public static ArrayList<String> getTypes() {
            ArrayList<String> list = new ArrayList<>();
            for (E_Type type : E_Type.values())
                list.add(type.getString());
            return list;
        }
    }

    public static ArrayList<String> getElementTypesString() {
        return E_Type.getTypes();
    }

    public DialogWindowCustom(String name) {
        super(name, D_Type.FormWindowCustom);

        if (cfg != null) {
            setTitle(cfg.getString("Title"));
            setScript(cfg.getString("Script"));
            @SuppressWarnings("unchecked")
            ArrayList<Object> list = (ArrayList<Object>) cfg.getList("Elements");
            for (Object obj : list) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> map = (Map<Object, Object>) obj;
                rawMap.add(map);
            }
        }
    }

    String script;

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void addElement(Element element) {
        addElement(element, getRawElements().size());
    }

    public void addElement(Element element, int idx) {
        if (element instanceof ElementLabel) {
            ElementLabel label = (ElementLabel) element;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            map.put("ElementType", E_Type.ElementLabel.getString());
            map.put("text", label.getText());
            rawMap.add(map);
        } else if (element instanceof ElementInput) {
            ElementInput input = (ElementInput) element;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            map.put("ElementType", E_Type.ElementInput.getString());
            map.put("text", input.getText());
            map.put("placeholder", input.getPlaceHolder());
            map.put("defaulttext", input.getDefaultText());
            rawMap.add(map);
        } else if (element instanceof ElementToggle) {
            ElementToggle toggle = (ElementToggle) element;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            map.put("ElementType", E_Type.ElementInput.getString());
            map.put("text", toggle.getText());
            rawMap.add(map);
        } else if (element instanceof ElementSlider) {
            ElementSlider slider = (ElementSlider) element;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            map.put("ElementType", E_Type.ElementSlider.getString());
            map.put("text", slider.getText());
            map.put("min", slider.getMin());
            map.put("max", slider.getMax());
            map.put("step", slider.getStep());
            map.put("defaultvalue", slider.getDefaultValue());
            rawMap.add(map);
        } else if (element instanceof ElementStepSlider) {
            ElementStepSlider stepslider = (ElementStepSlider) element;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            map.put("ElementType", E_Type.ElementStepSlider.getString());
            map.put("text", stepslider.getText());
            map.put("steps", stepslider.getSteps());
            map.put("defaultstep", stepslider.getDefaultStepIndex());
            rawMap.add(map);
        } else if (element instanceof ElementDropdown) {
            ElementDropdown dropdown = (ElementDropdown) element;
            HashMap<Object, Object> map = new HashMap<Object, Object>();
            map.put("ElementType", E_Type.ElementDropdown.getString());
            map.put("text", dropdown.getText());
            map.put("options", dropdown.getOptions());
            map.put("defaultoption", dropdown.getDefaultOptionIndex());
            rawMap.add(map);
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Element> getElements() {
        ArrayList<Element> elements = new ArrayList<>();
        for (Map<Object, Object> data : getRawElements()) {
            E_Type type = E_Type.get((String) data.get("ElementType"));

            if (type == E_Type.ElementLabel) {
                elements.add(new ElementLabel((String) data.get("text")));
            } else if (type == E_Type.ElementInput) {
                elements.add(new ElementInput((String) data.get("text"), (String) data.get("placeholder"), (String) data.get("defaulttext")));
            } else if (type == E_Type.ElementToggle) {
                elements.add(new ElementToggle((String) data.get("text")));
            } else if (type == E_Type.ElementSlider) {
                elements.add(new ElementSlider((String) data.get("text"), (float) data.get("min"), (float) data.get("max"), (int) data.get("step"), (float) data.get("defaultvalue")));
            } else if (type == E_Type.ElementStepSlider) {
                elements.add(new ElementStepSlider((String) data.get("text"), (List<String>) data.get("steps"), (int) data.get("defaultstep")));
            } else if (type == E_Type.ElementDropdown) {
                elements.add(new ElementDropdown((String) data.get("text"), (List<String>) data.get("options"), (int) data.get("defaultoption")));
            }
        }
        return elements;
    }

    ArrayList<Map<Object, Object>> rawMap = new ArrayList<>();

    public ArrayList<Map<Object, Object>> getRawElements() {
        return rawMap;
    }

    public void removeElement(int idx) {
        rawMap.remove(idx);
    }

    @Override
    public void save() {
        super.save();

        cfg.set("Title", getTitle());
        cfg.set("Script", getScript());
        cfg.set("Elements", getRawElements());

        cfg.save();
    }

    public Element convert(Element element, Player player, String[] s) {
        if (element instanceof ElementLabel) {
            ElementLabel label = (ElementLabel) element;
            label.setText(convert(label.getText(), player, s));
            return label;
        } else if (element instanceof ElementInput) {
            ElementInput input = (ElementInput) element;
            input.setText(convert(input.getText(), player, s));
            input.setPlaceHolder(convert(input.getPlaceHolder(), player, s));
            input.setDefaultText(convert(input.getDefaultText(), player, s));
            return input;
        } else if (element instanceof ElementToggle) {
            ElementToggle toggle = (ElementToggle) element;
            toggle.setText(convert(toggle.getText(), player, s));
            return toggle;
        } else if (element instanceof ElementSlider) {
            ElementSlider slider = (ElementSlider) element;

            return slider;
        } else if (element instanceof ElementStepSlider) {
            ElementStepSlider stepslider = (ElementStepSlider) element;

            return stepslider;
        } else if (element instanceof ElementDropdown) {
            ElementDropdown dropdown = (ElementDropdown) element;

            return dropdown;
        }
        return null;
    }

    public String convert(String text, Player player, String[] s) {
        text = replaceAll(text, s);
        text = CustomizerExecutor.parsePlaceHolder(null, player, text, null, null);
        return text;
    }

    @Override
    public FormWindow getForm(Player player, String[] s) {
        FormWindowCustom window = new FormWindowCustom(convert(getTitle(), player, s));
        for (Element element : getElements())
            window.addElement(convert(element, player, s));
        return window;
    }
}
