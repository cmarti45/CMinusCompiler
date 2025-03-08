package absyn;

public class NameTy extends Absyn {
    public static final int VOID = 0;
    public static final int INT = 1;
    public static final int BOOL = 2;
    public int type;
    public NameTy( int pos, int type){
        this.pos = pos;
        this.pos = pos;
        this.type = type;
    }

    public String toString(){
        return switch (this.type) {
            case VOID -> "VOID";
            case INT -> "INT";
            case BOOL -> "BOOL";
            default -> "INVALID TYPE";
        };
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
