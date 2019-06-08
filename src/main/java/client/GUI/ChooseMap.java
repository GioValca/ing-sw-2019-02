package client.GUI;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Match;

import java.io.File;

public class ChooseMap extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Choose map");

        //splitting the pane vertically
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        //importing the image
        File file = new File("." + File.separatorChar + "src" + File.separatorChar + "main"
                + File.separatorChar + "resources" + File.separatorChar + "ChooseMapBackground.png");
        Image image = new Image(file.toURI().toString());


        //if we need to resize the image
        ImageView iv = new ImageView(image);
        iv.setX(0);
        iv.setY(0);
        //iv.setFitHeight(250);
        //iv.setFitWidth(200);
        //iv.setPreserveRatio(true);


        HBox hBox = new HBox();
        splitPane.getItems().addAll(iv, hBox);

        hBox.getChildren().addAll();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);

        Scene scene= new Scene(splitPane,1000,1000);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
