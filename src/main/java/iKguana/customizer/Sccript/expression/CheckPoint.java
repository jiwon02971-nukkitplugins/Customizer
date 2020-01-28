package iKguana.customizer.Sccript.expression;

public abstract class CheckPoint {
    private int startIdx = -1;
    private int endIdx = -1;
    private String source = "";

    public CheckPoint() {
    }

    public CheckPoint(int startIdx, int endIdx, String source) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.source = source;
    }

    public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }

    public int getStartIdx() {
        return startIdx;
    }

    public void setEndIdx(int endIdx) {
        this.endIdx = endIdx;
    }

    public int getEndIdx() {
        return endIdx;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
