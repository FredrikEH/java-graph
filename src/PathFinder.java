import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PathFinder extends Application {

    private final MenuItem newMap = new MenuItem("New Map");
    private final MenuItem open = new MenuItem("Open");
    private final MenuItem save = new MenuItem("Save");
    private final MenuItem saveImage = new MenuItem("Save Image");
    private final MenuItem exit = new MenuItem("Exit");

    private final Button findPath = new Button("Find Path");
    private final Button showConnection = new Button("Show Connection");
    private final Button newPlace = new Button("New Place");
    private final Button newConnection = new Button("New Connection");
    private final Button changeConnection = new Button("Change Connection");

    private Image image = new Image("file:C:/Users/Dator/Documents/IntelliJ projects/Prog2ProjektGUI/europa.gif");
    private Stage stage;
    private final VBox veeBox = new VBox();
    private final Pane pane = new Pane();
    private final HBox buttonBox = new HBox();
    private final Scene scene = new Scene(veeBox);
    private Node node1;
    private Node node2;
    private boolean changes;
    private final HashMap<String, Node> nodes = new HashMap<>();
    private final ListGraph graph = new ListGraph();
    private final ImageView imageView = new ImageView(image);

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        Menu menu = new Menu("File");
        menu.getItems().addAll(newMap, open, save, saveImage, exit);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu);

        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);
        findPath.setDisable(true);
        showConnection.setDisable(true);
        newPlace.setDisable(true);
        newConnection.setDisable(true);
        changeConnection.setDisable(true);

        menu.setId("menuFile");
        menuBar.setId("menu");
        newMap.setId("menuNewMap");
        open.setId("menuOpenFile");
        save.setId("menuSaveFile");
        saveImage.setId("menuSaveImage");
        exit.setId("menuExit");
        findPath.setId("btnFindPath");
        showConnection.setId("btnShowConnection");
        newPlace.setId("btnNewPlace");
        changeConnection.setId("btnChangeConnection");
        newConnection.setId("btnNewConnection");
        pane.setId("outputArea");

        veeBox.getChildren().addAll(menuBar, buttonBox, pane);

        newMap.setOnAction(new NewMapHandler());
        open.setOnAction(new OpenHandler());
        save.setOnAction(new SaveHandler());
        saveImage.setOnAction(new SaveImageHandler());
        exit.setOnAction(new ExitHandlerMenu());
        stage.setOnCloseRequest(new ExitHandlerWindow());
        newPlace.setOnAction(new NewPlaceHandler());
        newConnection.setOnAction(new NewConnectionHandler());
        findPath.setOnAction(new FindPathHandler());
        changeConnection.setOnAction(new ChangeConnectionHandler());
        showConnection.setOnAction(new ShowConnectionHandler());

        stage.setTitle("PathFinder");
        stage.setScene(scene);
        stage.show();
    }

    class NewMapHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            newMap();
        }
    }

    class NewPlaceHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            scene.setCursor(Cursor.CROSSHAIR);
            newPlace.setDisable(true);
            pane.setOnMouseClicked(new ClickHandlerPlace());
        }
    }

    class ClickHandlerPlace implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){

            double x = event.getX();
            double y = event.getY();

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Name");
            dialog.setHeaderText("");
            dialog.setContentText("Name of place");
            Optional<String> result = dialog.showAndWait();
            String name = result.get();

            Node node = new Node(name, x, y);
            nodes.put(name, node);
            graph.add(node);
            node.setOnMouseClicked(new ClickHandlerMark());
            node.colorBlue();
            node.setId(name);
            pane.getChildren().add(node);

            setNameLabel(name, node);

            newPlace.setDisable(false);
            scene.setCursor(Cursor.DEFAULT);
            pane.setOnMouseClicked(null);
            changes = true;
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(node1 == null || node2 == null){
                alertError("Two places must be selected!");
                return;
            }
            if(graph.getEdgeBetween(node1, node2) != null){
                alertError("Connection already exists!");
                return;
            }
            MyAlert alert = new MyAlert();
            alert.setHeaderText("Connection from " + node1.getName() + " to " + node2.getName());
            Optional<ButtonType> result = alert.showAndWait();
            result.get();
            if(result.get() == ButtonType.CANCEL){
                event.consume();
            }
            else if(result.get() == ButtonType.OK){
                if(alert.getName() == null || !alert.amountIsInt()){
                    alertError("Field is either empty or does not contain an integer!");
                    event.consume();
                }
                graph.connect(node1, node2, alert.getName(), alert.getAmount());
                drawLine(node1, node2);
            }
            changes = true;
        }
    }

    class ClickHandlerMark implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event){

            Node node = (Node)event.getSource();

            if(node1 == null){
                node1 = node;
                node1.colorRed();
            }
            else if(node2 == null && node1 != node){
                node2 = node;
                node2.colorRed();
            }
            else if(node1 == node){
                node1.colorBlue();
                node1 = node2;
                node2 = null;
            }
            else if(node2 == node){
                node2.colorBlue();
                node2 = null;
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            try{
                PrintWriter printWriter = new PrintWriter(new FileWriter("europa.graph"));
                printWriter.println(image.getUrl());
                var setNode = nodes.values();
                int counter = 0;
                for(var node : setNode){
                    counter++;
                    String text = "";
                    text = text + node.getName() + ";" + node.getCenterX() + ";" + node.getCenterY() + ";";
                    if(counter == nodes.size()){
                        text = text.substring(0, text.length() - 1);
                    }
                    printWriter.write(text);
                }
                printWriter.println();
                for(var node : setNode){
                    Collection<Edge> edges = graph.getEdgesFrom(node);
                    for(Edge<Node> edge : edges){
                        printWriter.println(node.getName() + ";" + edge.getDestination().getName() + ";" + edge.getName() + ";" + edge.getWeight());
                    }
                }
                printWriter.close();
            }
            catch (FileNotFoundException e) {
                System.err.println("File not found!");
            }
            catch (IOException e) {
                System.err.println("IO exception!");
            }
        }
    }

    class OpenHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(changes){
                alertConfirmation("Unsaved changes, continue anyway?");
            }
            newMap();
            try{
                //inläsning av noder
                BufferedReader bufferedReader = new BufferedReader(new FileReader("C:/Users/Dator/Documents/IntelliJ projects/Prog2ProjektGUI/europa.graph"));
                String line = bufferedReader.readLine();
                image = new Image(line);
                imageView.setImage(image);
                line = bufferedReader.readLine();
                String[] tokens = line.split(";");
                for(int i = 0; i < tokens.length ; i+=3){
                    String nameNode = tokens[i];
                    double x = Double.parseDouble(tokens[i+1]);
                    double y = Double.parseDouble(tokens[i+2]);
                    Node node = new Node(nameNode, x, y);
                    graph.add(node);
                    nodes.put(nameNode, node);
                    node.setOnMouseClicked(new ClickHandlerMark());
                    node.colorBlue();
                    node.setId(nameNode);
                    pane.getChildren().add(node);
                    setNameLabel(nameNode, node);
                }
                //inläsning av kanter
                line = bufferedReader.readLine();
                while(line != null){
                    String start = line.split(";")[0];
                    String destination = line.split(";")[1];
                    String name = line.split(";")[2];
                    int weight = Integer.parseInt(line.split(";")[3]);
                    if(nodes.containsKey(start) && nodes.containsKey(destination)){
                        if(graph.getEdgeBetween(nodes.get(start), nodes.get(destination)) == null){
                            graph.connect(nodes.get(start), nodes.get(destination), name, weight);
                            drawLine(nodes.get(start), nodes.get(destination));
                        }
                    }
                    line = bufferedReader.readLine();
                }
            }
            catch (FileNotFoundException e) {
                System.err.println("File not found!");
            }
            catch (IOException e) {
                System.err.println("IO exception!");
            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            try{
                WritableImage image = pane.snapshot(null, null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", new File("capture.png"));
            }
            catch(IOException e){
                Alert alert= new Alert(Alert.AlertType.ERROR);
                System.err.println("IO-fel " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    class FindPathHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(node1 == null || node2 == null){
                alertError("Two places must be selected!");
                event.consume();
            }
            List<Edge<Node>> edges = graph.getPath(node1, node2);
            if(edges == null){
                alertError("No path exists!");
                event.consume();
            }
            String text = "";
            int totalWeight = 0;
            for(Edge<Node> edge : edges){
                text = text + "to " + edge.getDestination().getName() + " by " + edge.getName() + " takes " + edge.getWeight() + "\n";
                totalWeight = totalWeight + edge.getWeight();
            }
            TextArea textArea = new TextArea(text + "Total " + totalWeight);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setHeaderText("The Path from " + node1.getName() + " to " + node2.getName() + ":");
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(node1 == null || node2 == null){
                alertError("Two places must be selected!");
                return;
            }
            if(graph.getEdgeBetween(node1, node2) == null){
                alertError("Connection does not exist!");
                return;
            }
            MyAlert alert = new MyAlert();
            alert.setName(graph.getEdgeBetween(node1, node2).getName());
            alert.setHeaderText("Connection from " + node1.getName() + " to " + node2.getName());
            Optional<ButtonType> result = alert.showAndWait();
            result.get();
            if(result.get() == ButtonType.CANCEL){
                event.consume();
            }
            else if(result.get() == ButtonType.OK){
                if(alert.getName() == null || !alert.amountIsInt()){
                    alertError("Field is either empty or does not contain an integer!");
                    event.consume();
                }
                graph.setConnectionWeight(node1, node2, alert.getAmount());
            }
            changes = true;
        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event){
            if(node1 == null || node2 == null){
                alertError("Two places must be selected!");
                return;
            }
            if(graph.getEdgeBetween(node1, node2) == null){
                alertError("Connection does not exist!");
                return;
            }
            MyAlert alert = new MyAlert();
            alert.setName(graph.getEdgeBetween(node1, node2).getName());
            alert.setAmount(graph.getEdgeBetween(node1, node2).getWeight());
            alert.setHeaderText("Connection from " + node1.getName() + " to " + node2.getName());
            alert.showAndWait();
        }
    }

    class ExitHandlerMenu implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            if(changes){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning!");
                alert.setHeaderText("");
                alert.setContentText("Unsaved changes, exit anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                result.get();
                if(result.get() == ButtonType.CANCEL){
                    event.consume();
                }
                else if(result.get() == ButtonType.OK) {
                    System.exit(0);
                }
            }
            else if(!changes){
                System.exit(0);
            }
        }
    }

    class ExitHandlerWindow implements EventHandler<WindowEvent>{
        @Override
        public void handle(WindowEvent event){
            if(changes){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning!");
                alert.setHeaderText("");
                alert.setContentText("Unsaved changes, exit anyway?");
                Optional<ButtonType> result = alert.showAndWait();
                result.get();
                if(result.get() == ButtonType.CANCEL){
                    event.consume();
                }
                else if(result.get() == ButtonType.OK) {
                    System.exit(0);
                }
            }
            else if(!changes){
                System.exit(0);
            }
        }
    }

    public void alertError(String a){
        Alert alert = new Alert(Alert.AlertType.ERROR, a);
        alert.setTitle("Error!");
        alert.setHeaderText("");
        alert.showAndWait();
    }

    public void alertConfirmation(String a){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, a);
        alert.setTitle("Warning!");
        alert.setHeaderText("");
        alert.showAndWait();
    }

    public void newMap(){
        pane.getChildren().clear();
        Set<Node> set = graph.getNodes();
        for(Node node : set){
            graph.remove(node);
        }
        nodes.clear();
        node1 = null;
        node2 = null;
        pane.getChildren().add(imageView);
        stage.sizeToScene();
        stage.centerOnScreen();
        findPath.setDisable(false);
        showConnection.setDisable(false);
        newPlace.setDisable(false);
        newConnection.setDisable(false);
        changeConnection.setDisable(false);
    }

    public void setNameLabel(String name, Node node){
        Label label = new Label(name);
        label.setLayoutX(node.getCenterX() + 10);
        label.setLayoutY(node.getCenterY());
        label.setStyle("-fx-font-size: 20");
        label.setOpacity(1);
        label.setDisable(true);
        pane.getChildren().add(label);
    }

    public void drawLine(Node node1, Node node2){
        Line line = new Line(node1.getCenterX(), node1.getCenterY(), node2.getCenterX(), node2.getCenterY());
        line.setStrokeWidth(4);
        pane.getChildren().add(line);
        line.setDisable(true);
    }
}
