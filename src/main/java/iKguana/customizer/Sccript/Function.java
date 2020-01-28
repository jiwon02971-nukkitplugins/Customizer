package iKguana.customizer.Sccript;

import iKguana.customizer.Sccript.expression.*;

public class Function extends Expression {
    Name name;
    Arguments arguments;
    Block block;
    String source;

    public Function(Name name, Arguments arguments, Block block, String source) {
        this.name = name;
        this.arguments = arguments;
        this.block = block;
        this.source = source;

        if (isValid())
            Parser.parse(this, block.getSource());
    }

    public boolean isValid() {
        if (name.getStartIdx() < name.getEndIdx() && name.getEndIdx() < arguments.getStartIdx() &&
                arguments.getStartIdx() < arguments.getEndIdx() && arguments.getEndIdx() < block.getStartIdx() &&
                block.getStartIdx() < block.getEndIdx()) {
            String gap1 = source.substring(name.getEndIdx(), arguments.getStartIdx()).trim();
            String gap2 = source.substring(arguments.getEndIdx(), block.getStartIdx()).trim();

            if (gap1.length() == 0 && gap2.length() == 0)
                return true;
        }
        return false;
    }
}
