import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 *
 * @author Clark Kent
 */
public class CRabeFINAL extends Application
{
    private boolean isPlaying = false;
    private Preferences pref;
    private int highscore;
    private int segments;
    private int rockCount;
    private Label mLabel;
    private Canvas mPrimary = new Canvas(480, 480);
    private GraphicsContext gc = mPrimary.getGraphicsContext2D();
    private ImageView imv = new ImageView();
    private MenuItem pauseGameMenuItem;
    private MenuItem goGameMenuItem;
    private SettingsLayoutController controller;
    {
        try {
            controller = new SettingsLayoutController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Playfield field;
    private AnimationTimer timer;

    @Override
    public void start(Stage primaryStage)
    {
        pref = Preferences.userNodeForPackage(getClass());
        controller.readPref(getClass());
        buildSettings();
        pref.addPreferenceChangeListener(new PreferenceChangeListener()
        {
            @Override
            public void preferenceChange(PreferenceChangeEvent evt)
            {
                //controller.storePref(getClass());
                resetGame();
            }
        });

        final StackPane stPane = new StackPane();
        final BorderPane root = new BorderPane();
        field = new Playfield(segments, rockCount, mPrimary);
        field.playFieldSetUp();


        stPane.setPrefSize(mPrimary.getWidth(), mPrimary.getHeight());
        stPane.setAlignment(Pos.CENTER);
        stPane.getChildren().add(mPrimary);
        stPane.getChildren().add(field);


        //draws background
        imv.setImage(new Image("images/background.png"));
        gc.drawImage(imv.getImage(),0,0, mPrimary.getWidth(), mPrimary.getHeight());

        root.getCenter();
        root.setCenter(stPane);
        root.setPrefSize(mPrimary.getWidth(), mPrimary.getHeight());
        root.setTop(buildMenuBar());


        mLabel = new Label("Score = " + field.score + " Lives = " + field.lives);


        ToolBar toolBar = new ToolBar(mLabel);
        root.setBottom(toolBar);
        Scene scene = new Scene(root, mPrimary.getWidth(), mPrimary.getHeight() + 40);
        scene.setOnKeyPressed(event -> onKeyPressed(event));
        scene.setOnMouseClicked(event -> onMouseClick(event));
        primaryStage.setTitle("Centipede Pirates");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        timer = new AnimationTimer()
        {
            private long lastUpdate = 0;
            private int counter = 0;

            @Override
            public void handle(long now)
            {

                if(now - lastUpdate >= 20_000)
                {
                    if(counter == 5 && field.lives > 0)
                    {
                        counter = 0;
                        field.updateOrcs();
                    }

                    if(!field.isAlive && field.lives > 0)
                    {
                        field.isAlive = true;
                        resetGame();
                    }
                    else if(!field.isAlive && field.lives == 0)
                    {
                        gameOver();
                    }
                    else {
                        field.updatePlayField();
                        updateLabel("Score = " + field.score + " Lives = " + field.lives);
                        lastUpdate = now;
                        counter++;
                    }
                }
            }
        };

    enableDisable();
    }


    private MenuBar buildMenuBar()
    {
        MenuBar menuBar = new MenuBar() ;

        //File Menu Items

        Menu fileMenu = new Menu("_File") ;
        MenuItem quitMenuItem = new MenuItem("_Quit") ;
        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.CONTROL_DOWN));
        quitMenuItem.setOnAction(actionEvent -> Platform.exit()) ;
        fileMenu.getItems().add(quitMenuItem) ;


        //Game Menu Items

        Menu gameMenu = new Menu("_Game");


        MenuItem newGameMenuItem = new MenuItem("_New");
        newGameMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N,
                KeyCombination.CONTROL_DOWN));
        newGameMenuItem.setOnAction(actionEvent -> onNew());

        goGameMenuItem = new MenuItem("_Go");
        goGameMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.G,
                KeyCombination.CONTROL_DOWN));
        goGameMenuItem.setOnAction(actionEvent -> onGo());

        pauseGameMenuItem = new MenuItem("_Pause");
        pauseGameMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P,
                KeyCombination.CONTROL_DOWN));
        pauseGameMenuItem.setOnAction(actionEvent -> onPause());


        MenuItem highScoreGameMenuItem = new MenuItem("_High Score");
        highScoreGameMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.H,
                KeyCombination.CONTROL_DOWN));
        highScoreGameMenuItem.setOnAction(actionEvent -> onHighScore(false));

        MenuItem resetHSGameMenuItem = new MenuItem("_Reset High Score");
        newGameMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R,
                KeyCombination.CONTROL_DOWN));
        resetHSGameMenuItem.setOnAction(actionEvent -> onHighScore(true));

        MenuItem settingsGameMenuItem = new MenuItem("_Settings");
        newGameMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN));
        settingsGameMenuItem.setOnAction(actionEvent ->
        {
                isPlaying = false;
                onSettings();
                });


        //Separators

        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();


        gameMenu.getItems().addAll(newGameMenuItem, separator1, goGameMenuItem, pauseGameMenuItem, highScoreGameMenuItem,
                resetHSGameMenuItem, separator2, settingsGameMenuItem);



        Menu helpMenu = new Menu("_Help") ;
        MenuItem aboutMenuItem = new MenuItem("_About") ;
        aboutMenuItem.setOnAction(actionEvent -> onAbout()) ;
        helpMenu.getItems().add(aboutMenuItem) ;
        menuBar.getMenus().addAll(fileMenu, gameMenu,helpMenu) ;
        return menuBar ;
    }

    private void onAbout()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION) ;
        alert.setTitle("About") ;
        alert.setHeaderText("Clark D. Rabe, CSCD 370 Final, Fall 2018") ;
        alert.showAndWait() ;
    }

    private void onNew()
    {
        resetGame();
    }

    private void onGo()
    {

        isPlaying = true;
        timer.start();
        enableDisable();

    }

    private void onPause()
    {
        isPlaying = false;
        timer.stop();
        enableDisable();
    }

    private void enableDisable()
    {
        if(!isPlaying)
        {
            pauseGameMenuItem.setDisable(true);
            goGameMenuItem.setDisable(false);
        }
        else {
            pauseGameMenuItem.setDisable(false);
            goGameMenuItem.setDisable(true);
        }
    }
    private void onHighScore(boolean reset)
    {
        if(reset)
        {
            highscore = 0;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("High Score Reset");
            alert.setHeaderText("High score was reset!");
            alert.showAndWait();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("HIGH SCORE");
        alert.setHeaderText("Current High Score: " + highscore);
        alert.showAndWait();

    }


    private void onSettings()
    {
        isPlaying = false;
        timer.stop();
        controller.setTitle("Application Settings");
        controller.initModality(Modality.WINDOW_MODAL);
        controller.show();
        buildSettings();
    }


    //Player controls
    private void onKeyPressed(KeyEvent event)
    {

        if(event.getCode() == KeyCode.LEFT && isPlaying)
        {
           field.updatePlayer("left");
        }
        else if(new KeyCodeCombination(KeyCode.RIGHT).match(event) && isPlaying)
        {
            field.updatePlayer("right");
        }
        else if(new KeyCodeCombination(KeyCode.SPACE).match(event) && isPlaying)
        {
            field.updatePlayer("space");
        }


    }

    //Player firing controls
    private void onMouseClick(MouseEvent event)
    {
        MouseButton button = event.getButton();

        if(button.equals(MouseButton.PRIMARY) && isPlaying)
        {
            field.updatePlayer("click");
        }
        else if(button.equals(MouseButton.SECONDARY))
        {
            if(!isPlaying)
            {
                isPlaying = true;
                timer.start();
            }
            else
            {
                isPlaying = false;
                timer.stop();
            }

        }
    }


    private void buildSettings()
    {
        segments = settingsData.numSegments;
        rockCount = settingsData.rockCount;
        highscore = settingsData.highScore;
    }

    public void resetGame()
    {
        isPlaying = false;
        timer.stop();
        buildSettings();
        field.clearPlayField();
        field.playFieldSetUp();
    }

    public void newGame()
    {
        isPlaying = false;
        timer.stop();
        buildSettings();
        field.clearPlayField();
        field.playFieldSetUp();
        this.field.score = 0;
        this.field.lives = 3;
    }

    public void updateLabel(String s)
    {
        mLabel.setText(s);
    }

    public void gameOver()
    {
        timer.stop();

        if(field.score > highscore)
        {
            highscore = field.score;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("New High Score!!");
            alert.setHeaderText("New High Score =  " + highscore);
            alert.show();

        }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over. Your Score =  " + highscore);
            alert.show();
            alert.setOnHidden(e -> {
                if(alert.getResult() == ButtonType.YES)
                    newGame();
                else
                    alert.close();
            });
    }

    public static void main(String[] args)
    {
        launch(args);
    }

}

