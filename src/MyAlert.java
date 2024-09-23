import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MyAlert extends Alert {

    private TextField nameField = new TextField();
    private TextField amountField = new TextField();

    public MyAlert(){
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Time:"), amountField);
        getDialogPane().setContent(grid);
        setTitle("Connection");
    }

    public String getName(){
        return nameField.getText();
    }

    public void setName(String name){
        nameField.setText(name);
        nameField.setEditable(false);
    }

    public int getAmount(){
        return Integer.parseInt(amountField.getText());
    }

    public void setAmount(int amount){
        amountField.setText("" + amount);
        amountField.setEditable(false);
    }

    public boolean amountIsInt(){
        try{
            Integer.parseInt(amountField.getText());
            return true;
        }
        catch(NumberFormatException nfe){
            return false;
        }
    }
}
