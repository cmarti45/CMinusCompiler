package absyn;

public class BoolExp extends Exp {
    public final static int FALSE = 0;
    public final static int TRUE = 1;
    public String value;

    public BoolExp(int pos, String value ) {
        this.pos = pos;
        this.value = value;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
