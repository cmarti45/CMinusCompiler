package absyn;

public class ArrayDec extends VarDec{
    public int size;
    public ArrayDec( int pos, int offset, int nestLevel, NameTy typ, String name, int size){
        this.pos = pos;
        this.offset = offset;
        this.nestLevel = nestLevel;
        this.type = typ;
        this.name = name;
        this.size = size;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

    public static ArrayDec type(Exp exp, NameTy type, int size) {
        return switch (type.type) {
            case NameTy.VOID -> new ArrayDec(exp.pos, 0, 0, new NameTy(exp.pos, NameTy.VOID), "", size);
            case NameTy.INT -> new ArrayDec(exp.pos, 0, 0, new NameTy(exp.pos, NameTy.INT), "", size);
            case NameTy.BOOL -> new ArrayDec(exp.pos, 0, 0, new NameTy(exp.pos, NameTy.BOOL), "", size);
            default -> new ArrayDec(exp.pos, 0, 0, new NameTy(exp.pos, NameTy.ERROR), "", size);
        };

    }

    @Override
    public String toString(){
        return name + ": " + type.toString().toLowerCase() + "[" + this.size + "]\t\t"  + "[" + this.nestLevel + "]:[" + this.offset + "]";
    }
}
