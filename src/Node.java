import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Node extends Circle {

    private String name;

    public Node(String name, double x, double y){
        super(x, y, 10);
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void colorBlue(){
        this.setFill(Color.BLUE);
    }

    public void colorRed(){
        this.setFill(Color.RED);
    }
}
