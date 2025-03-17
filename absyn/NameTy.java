package absyn;

public class NameTy extends Absyn {
    public static final int ERROR = -1;
    public static final int VOID = 0;
    public static final int INT = 1;
    public static final int BOOL = 2;
    public int type;
    public NameTy( int pos, int type){
        this.pos = pos;
        this.type = type;
    }

    @Override
    public String toString(){
        return switch (this.type) {
            case VOID -> "VOID";
            case INT -> "INT";
            case BOOL -> "BOOL";
            default -> "INVALID TYPE";
        };
    }

    public boolean equals(NameTy n1){
        return this.type == n1.type;
    }

    public boolean equals(int n1){
        return this.type == n1;
    }

    public boolean isError(){
        return type == ERROR;
    }

    public boolean isVoid(){
        return type == VOID;
    }

    public boolean isInt(){
        return type == INT;
    }

    public boolean isBool(){
        return type == BOOL;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
