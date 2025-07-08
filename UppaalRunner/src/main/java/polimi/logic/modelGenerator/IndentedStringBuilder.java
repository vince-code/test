package polimi.logic.modelGenerator;

public class IndentedStringBuilder {
    private final StringBuilder sb;
    private int indentLevel = 0;
    private static final String INDENT = "\t";
    private static final String NEW_LINE = "\n";

    public IndentedStringBuilder() {
        sb = new StringBuilder();
    }

    public IndentedStringBuilder(String string) {
        sb = new StringBuilder(string);
    }

    public IndentedStringBuilder(Integer capacity) {
        if (capacity != null){
            sb = new StringBuilder(capacity);
        } else {
            sb = new StringBuilder();
        }
    }

    public IndentedStringBuilder(Integer capacity, int initialIndentationLevel) {
        if (capacity != null){
            sb = new StringBuilder(capacity);
        } else {
            sb = new StringBuilder();
        }
        this.indentLevel = initialIndentationLevel;
    }

    public IndentedStringBuilder append(String str) {
        sb.append(str);
        return this;
    }

    public IndentedStringBuilder appendIndented(String str) {
        indent();
        sb.append(str);
        return this;
    }

    public IndentedStringBuilder append(int value) {
        sb.append(value);
        return this;
    }

    public IndentedStringBuilder append(String str, int start, int end) {
        sb.append(str, start, end);
        return this;
    }

    public IndentedStringBuilder append(IndentedStringBuilder indentedStringBuilder, int start, int end) {
        sb.append(indentedStringBuilder.sb, start, end);
        return this;
    }

    public IndentedStringBuilder append(IndentedStringBuilder indentedStringBuilder) {
        sb.append(indentedStringBuilder.toString());
        return this;
    }

    public IndentedStringBuilder appendLine(String line) {
        indent();
        sb.append(line).append(NEW_LINE);
        return this;
    }

    public IndentedStringBuilder appendAssign(String var, String expr) {
        indent();
        sb.append(var).append(" = ").append(expr).append(";").append(NEW_LINE);
        return this;
    }

    public IndentedStringBuilder appendAssign(String var, int expr) {
        indent();
        sb.append(var).append(" = ").append(expr).append(";").append(NEW_LINE);
        return this;
    }

    public IndentedStringBuilder appendRawLine(String raw) {
        sb.append(raw).append(NEW_LINE);
        return this;
    }

    public IndentedStringBuilder increaseIndentation() {
        indentLevel++;
        return this;
    }

    public IndentedStringBuilder decreaseIndentation() {
        if (indentLevel > 0) indentLevel--;
        return this;
    }

    private void indent() {
        sb.append(INDENT.repeat(indentLevel));
    }

    public int length() {
        return sb.length();
    }

    public IndentedStringBuilder newLine() {
        sb.append(NEW_LINE);
        return this;
    }

    public IndentedStringBuilder newLine(int numberOfLines) {
        sb.append(NEW_LINE.repeat(numberOfLines));
        return this;
    }

    public int getCurrentIndentation(){
        return indentLevel;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
