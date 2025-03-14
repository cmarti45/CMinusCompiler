package absyn;

public class FunctionDec extends Dec {
    public NameTy result;
    public String func;
    public VarDecList params;
    public Exp body;
    public FunctionDec( int pos, NameTy result, String func, VarDecList params, Exp body){
        this.pos = pos;
        this.result = result;
        this.func = func;
        this.body = body;
        this.params = params;
    }

    public void accept( AbsynVisitor visitor, int level ) {
        visitor.visit( this, level );
    }

    @Override
    public String toString(){
        String s = "Function: " + this.result.toString().toLowerCase() + " " + this.func;
        if (this.params.head == null){
            return s;
        }
        VarDecList p = this.params;
        StringBuilder params = new StringBuilder(p.head.type.toString());
        if (p.head instanceof ArrayDec){
            params.append(" *");
        }
        while (p.tail != null){
            p = p.tail;
            params.append(", ").append(p.head.type);
            if (p.head instanceof ArrayDec){
                params.append(" *");
            }
        }
        return func + ": (" + params.toString().toLowerCase() + ") -> " + result.toString().toLowerCase();
    }
}
