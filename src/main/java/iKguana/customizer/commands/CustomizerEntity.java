package iKguana.customizer.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;
import iKguana.simpledialog.SimpleDialog;

public class CustomizerEntity extends CustomizerBase {
	public CustomizerEntity() {
		setName("CustomizerCommand");

		CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals(CT.getMessage(getName(), "label"))) {
			if (sender.isPlayer()) {
				FormWindowSimple window = new FormWindowSimple("커스텀 엔티티", "사용하고싶은 기능을 클릭해주세요.");
				window.addButton(new ElementButton("엔티티 추가"));
				window.addButton(new ElementButton("엔티티 목록"));
				SimpleDialog.sendDialog(this, "form_ent_menu", (Player) sender, window);
			} else
				sender.sendMessage(CT.getMessage("__Public", "NOT_IN_GAME"));
			return true;
		}
		return false;
	}

	public void form_ent_menu(PlayerFormRespondedEvent event, Object data) {

	}
}
