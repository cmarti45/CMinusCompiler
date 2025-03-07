package absyn;

public class ExpVarDecList extends Exp{
    public VarDecList dec;
    public ExpVarDecList(VarDecList dec){
        this.dec=dec;
    }
    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
