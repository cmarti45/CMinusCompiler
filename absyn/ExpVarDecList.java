package absyn;

public class ExpVarDecList extends Exp{
    public VarDecList dec;
    public ExpVarDecList(int row, int col, VarDecList dec){
        this.row = row;
        this.col = col;
        this.dec=dec;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
