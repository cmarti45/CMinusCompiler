package absyn;

public class SimpleDec extends VarDec{
    public SimpleDec( int pos, NameTy typ, String name){
        this.pos = pos;
        this.type = typ;
        this.name = name;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

    @Override
    public String toString(){
        return this.name + ": " + this.type.toString().toLowerCase();
    }

    public static SimpleDec type(Exp exp, NameTy type) {
        return switch (type.type) {
            case NameTy.VOID -> new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.VOID), "");
            case NameTy.INT -> new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.INT), "");
            case NameTy.BOOL -> new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.BOOL), "");
            default -> new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.ERROR), "");
        };

    }

    public static SimpleDec tError(Exp exp) {
        return new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.ERROR), "");
    }

    public static SimpleDec tVoid(Exp exp) {
        return new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.VOID), "");
    }

    public static SimpleDec tInt(Exp exp) {
        return new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.INT), "");
    }

    public static SimpleDec tBool(Exp exp) {
        return new SimpleDec(exp.pos, new NameTy(exp.pos, NameTy.BOOL), "");
    }
}
