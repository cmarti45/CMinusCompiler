package absyn;

public class CompoundExp extends Absyn {
    public VarDecList vars;
    public ExpList exps;
    public CompoundExp(int row, int col, VarDecList vars, ExpList exps){
        this.vars = vars;
        this.exps = exps;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }
}
