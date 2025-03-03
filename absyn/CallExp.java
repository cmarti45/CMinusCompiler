package absyn;

public class CallExp extends Exp {
    public String id;
    public ExpList args;

    public CallExp( int row, int col, String id, ExpList args ) {
        this.row = row;
        this.col = col;
        this.id = id;
        this.args = args;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
