package test;

/**
 * Created by Cfw on 2016/7/16.
 */
public class Main extends Father{

    public static void main(String[] args){
        Main m = new Main();
        m.name = "main";
        m.print();

    }
}

class Father{
    protected String name;

    protected void print(){
        System.out.println(this.name);
    }
}
