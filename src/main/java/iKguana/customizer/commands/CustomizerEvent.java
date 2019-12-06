package iKguana.customizer.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import iKguana.customizer.Customizer;
import iKguana.customizer.CustomizerCommands;
import iKguana.customizer.CustomizerEvents;
import iKguana.customizer.interfaces.CustomizerBase;
import iKguana.customizer.tools.CT;

import java.io.File;

public class CustomizerEvent extends CustomizerBase {
    public CustomizerEvent() {
        setName("CustomizerEntity");

        new EventManager(new File(Customizer.getInstance().getDataFolder(), getName() + ".yml"));

        //TODO REGISTER ALL EVENTS

        CustomizerCommands.getInstance().registerCommand(this, CT.getMessage(getName(), "label"), CT.getMessage(getName(), "description"), CT.getMessage(getName(), "usage", CT.getMessage(getName(), "label")), CT.getMessage(getName(), "permission"));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}

class EventManager {
    public EventManager(File CFG_PATH) {

    }
}