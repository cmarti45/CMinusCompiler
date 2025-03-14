package absyn;

public abstract class VarDec extends Dec {
    public NameTy type;
    public String name;

    @Override
    public abstract String toString();
}
