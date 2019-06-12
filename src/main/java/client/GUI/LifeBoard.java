package client.GUI;

import client.remoteController.SenderClientRemoteController;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Match;

import java.io.File;

public class LifeBoard extends Application {
    private Match match;
    SenderClientRemoteController remoteController;


    @Override
    public void start(Stage stage) throws Exception {

        //stage.initModality(Modality.APPLICATION_MODAL);
       // stage.setTitle("Life Player '"+remoteController.getNickname()+"'");
        stage.setTitle("Prova");
        stage.setMinWidth(410);
        stage.setMinHeight(210);

        StackPane stackPane = new StackPane();

        File file0 = new File("." + File.separatorChar + "src" + File.separatorChar + "main"
                + File.separatorChar + "resources" + File.separatorChar + "lifeBoards" + File.separatorChar + "LifeBoardNormal" + File.separatorChar +/* File.separatorChar + match.getPlayer(remoteController.getNickname()).getId() +*/ "1.png");
        Image image0 = new Image(file0.toURI().toString());
        ImageView iv0 = new ImageView(image0);
        iv0.setFitHeight(200);
        iv0.setFitWidth(400);
        iv0.setPreserveRatio(true);

        stackPane.setAlignment(Pos.CENTER);

        Circle blueCircle = new Circle(40,40,7);
        blueCircle.setFill(Color.TRANSPARENT);

        Circle redCircle = new Circle(40,40,7);
        redCircle.setFill(Color.TRANSPARENT);

        Circle yCircle = new Circle(40,40,7);
        yCircle.setFill(Color.YELLOW);


        HBox testLife = new HBox(7);
        testLife.getChildren().addAll(blueCircle, redCircle, yCircle);
        testLife.setAlignment(Pos.CENTER_LEFT);

        stackPane.setStyle("-fx-background-color: #191a17");
        stackPane.getChildren().addAll(iv0, testLife);

        Scene scene = new Scene(stackPane);

        stage.setScene(scene);
        //stage.showAndWait();
        stage.show();
    }

    public void setRemoteController(SenderClientRemoteController remoteController) {
        this.remoteController = remoteController;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
