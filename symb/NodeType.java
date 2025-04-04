package symb;

import absyn.Dec;

public class NodeType {
    String name;
    public Dec def;
    int level;

    public NodeType(String name, Dec def, int level){
        this.name = name;
        this.def = def;
        this.level = level;
    }

    @Override
    public String toString(){
        return this.level + ": " + this.name;
    }
}
