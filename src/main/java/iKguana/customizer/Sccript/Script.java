package iKguana.customizer.Sccript;

import iKguana.customizer.Sccript.expression.Expression;
import iKguana.customizer.Sccript.expression.Parser;

import java.util.ArrayList;

public class Script {
    String raw = "";
    Expression sources = new Expression();

    public Script(String raw) {
        Parser.parse(sources, raw);
    }

    public void run() {
        sources.run();
    }
}
