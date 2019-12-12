package iKguana.customizer.Sccript;

public class Script {
    String raw = "";
    Expressions expressions;

    public Script(String raw) {
        expressions = Parser.parse(raw);
    }

    public void run() {

    }
}
