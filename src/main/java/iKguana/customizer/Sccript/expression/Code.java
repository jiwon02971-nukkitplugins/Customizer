package iKguana.customizer.Sccript.expression;

public class Code {
    String source;
    int idx;

    public void Code(String source) {
        this.source = source;
        idx = 0;
    }

    public boolean hasNext() {
            return false;
    }

    public Name getNext() {
        return new Name();
    }
}
