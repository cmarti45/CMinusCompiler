package absyn;

public abstract class VarDec extends Dec {
    public String name;
    public int offset;
    public int nestLevel;

    @Override
    public abstract String toString();
}
