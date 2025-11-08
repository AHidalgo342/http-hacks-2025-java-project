import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class JavaFXTest
        extends Application
{
    @Override
    public void start(final Stage primaryStage)
    {
        final Label label;
        final Scene scene;

        label = new Label("Hello JavaFX!");
        scene = new Scene(label,
                          300,
                          200);

        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(final String[] args)
    {
        launch(args);
    }
}
