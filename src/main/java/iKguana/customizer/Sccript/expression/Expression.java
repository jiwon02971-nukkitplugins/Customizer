package iKguana.customizer.Sccript.expression;

import iKguana.customizer.Sccript.Executable;
import iKguana.customizer.Sccript.Function;

import java.util.ArrayList;

public class Expression extends Executable {
    public enum Type {
        FUNCTION("func"), IF("if"), IFELSE("else if"), METHOD("%NAME% %ARGUMENTS%");
        String t;

        Type(String t) {
            this.t = t;
        }

        public String getHead() {
            return t.substring(0, t.indexOf(" "));
        }

        public String getFormat() {
            return t;
        }
    }

    ArrayList<Executable> expressions = new ArrayList<>();

    public void addFunction(Name name, Arguments argument, Block block, String source) {
        expressions.add(new Function(name, argument, block, source));
    }

    public void addMethod(Name name, Arguments arguments) {
    }

    public void addIF(Arguments arguments) {
    }

    @Override
    public void run() {
        for (Executable exe : expressions)
            exe.run();
    }
}
