import java.util.ArrayList;

public class Stack extends ArrayList<Integer> {
    public Stack(){
        super();
    }

    public void push(int node){
        add(node);
    }

    public int pop(){
        return remove(size()-1);
    }


}
