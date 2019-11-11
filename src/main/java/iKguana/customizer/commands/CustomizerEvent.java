package iKguana.customizer.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import iKguana.customizer.interfaces.CustomizerBase;

import java.io.File;

public class CustomizerEvent extends CustomizerBase {
    public CustomizerEvent(){

    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }
}

class EventManager{
    public EventManager(File CFG_PATH){

    }

}