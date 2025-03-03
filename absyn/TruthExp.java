package absyn;

public class TruthExp extends Exp {
    public String value;

    public TruthExp( int row, int col, String value ) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
