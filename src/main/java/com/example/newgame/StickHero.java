package com.example.newgame;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.control.Button;

public class StickHero implements GameObserver{
    private Scene scene;
    private Group group;
    private Rectangle Rectangle1;
    private Rectangle Rectangle2;
    private Rectangle feed;
    private Line stick;
    private int distance;
    private int toGo, cycle;
    private boolean adjustment;
    private boolean isEnd = false;
    private Random random;
    private Rectangle Foot_Left;
    private Rectangle Foot_Right;
    private Rectangle Body;
    private Circle Head;
    private int Score = 0;
    private Label Score_Label;
    private int Cherry_Count = 0;
    private Label Cherry_Count_Label;
    private String Player_Name;
    private int Record;
    private String bestPlayerName;
    private Button restartButton;
    private List<GameObserver> observers = new ArrayList<>();

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }
    public Group getGroup() {
        return group;
    }
    public Scene getScene() {
        return scene;
    }

    public StickHero(Scene scene, Group group, String playerName) {
        this.scene = scene;
        this.group = group;
        this.Player_Name = playerName + " ";

        Rectangle1 = new Rectangle();
        Rectangle1.setFill(Color.SLATEGRAY);
        Rectangle2 = new Rectangle();
        Rectangle2.setFill(Color.SLATEGRAY);
        feed = new Rectangle(16, 16);
        fillWithImage();
        stick = new Line();
        stick.setStroke(Color.GOLDENROD);
        stick.setStrokeWidth(2.0);
        Foot_Left = new Rectangle(5, 25);
        Foot_Right = new Rectangle(5, 25);
        Body = new Rectangle(15, 20);
        Head = new Circle(5);
        restartButton = new javafx.scene.control.Button("Restart Game");
        restartButton.setLayoutX(300); // Adjust the layout position as needed
        restartButton.setLayoutY(250); // Adjust the layout position as needed
        restartButton.setVisible(false);
        restartButton.setOnAction(event -> Restart_Game());


        Foot_Left.setX(-20);
        Foot_Right.setX(-20);
        Body.setX(-20);
        Head.setCenterX(-20);
        feed.setX(-20);


        Rectangle1.setX(0);

        Score_Label = new Label(String.valueOf(Score));
        Score_Label.setLayoutX(200);
        Score_Label.setLayoutY(100);
        Score_Label.setFont(Font.font("Arial", 22));
        Score_Label.setTextFill(Color.WHITE);

        Cherry_Count_Label = new Label(String.valueOf(Cherry_Count));
        Cherry_Count_Label.setLayoutX(500);
        Cherry_Count_Label.setLayoutY(100);
        Cherry_Count_Label.setFont(Font.font("Arial", 22));
        Cherry_Count_Label.setTextFill(Color.ORANGE);

        group.getChildren().add(Cherry_Count_Label);

        random = new Random(System.currentTimeMillis());
        group.getChildren().addAll(Rectangle1, Rectangle2, Score_Label,
                stick, Foot_Left, Foot_Right, Body, Head, feed);
        group.getChildren().add(restartButton);

        Set_Randoms();
        Set_Command_Adjustment();
    }
    private void fillWithImage() {
        try (InputStream stream = new FileInputStream("img/cherry.png")) {
            javafx.scene.image.Image fxImage = new javafx.scene.image.Image(stream);
            feed.setFill(new ImagePattern(fxImage));
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., show an error message)
        }
    }

    private void Set_BestPlayer() {
        java.util.List<String> names = new ArrayList<>();
        java.util.List<Integer> scores = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader
                    (new File("files/best.txt"))); // you may need to change this
            String nameTemp = br.readLine();
            String scoreTemp = br.readLine();

            String second = br.readLine();

            names.add(nameTemp);
            scores.add(Integer.parseInt(scoreTemp));

            while (second != null) {
                names.add(second);
                scores.add(Integer.parseInt(br.readLine()));

                second = br.readLine();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        Record = 0;
        for (int i = 0; i < scores.size(); i++)
            if (scores.get(i) > Record) {
                Record = scores.get(i);
                if (Score > Record){
                    bestPlayerName = "Current Highscore By " + Player_Name + " ";
                    Record = Score;
                }else {
                    bestPlayerName = "Current Highscore By " + names.get(i) +" ";
                }
            }
    }

    private void Play_Game() {
        if (stick.getEndX() - stick.getStartX() < distance ||
                stick.getEndX() - stick.getStartX() > distance + Rectangle2.getWidth())
            isEnd = true;

        Move_Player();
        Tell_Observers();
    }

    private void Move_Player() {
        toGo = (int) (stick.getEndX() - stick.getStartX() + Rectangle1.getWidth() / 2);
        cycle = toGo / 15;
        cycle *= 2; // :|

        for (int i = 0; i < cycle; i++) {
            Cycle1_go();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Fix_Position();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (isEnd) {
            GameOver();
            restartButton.setVisible(true);
            Save_Record();
            Show_Record();
        }
        else {
            ++Score;
            Platform.runLater(() -> Score_Label.setText(String.valueOf(Score)));
            Set_Randoms();
        }
    }

    private void Show_Record() {
        Label playerNameLabel = new Label(Player_Name);
        playerNameLabel.setFont(Font.font("Arial", 20));
        playerNameLabel.setTextFill(Color.WHITE);
        Label bestPlayerNameLabel = new Label(bestPlayerName);
        bestPlayerNameLabel.setFont(Font.font("Arial", 20));
        bestPlayerNameLabel.setTextFill(Color.WHITE);
        Label bestPlayerScoreLabel = new Label(String.valueOf(Record));
        bestPlayerScoreLabel.setFont(Font.font("Arial", 20));
        bestPlayerScoreLabel.setTextFill(Color.WHITE);

        Platform.runLater(() -> {
            HBox hBox1 = new HBox(playerNameLabel, new Separator(Orientation.VERTICAL), Score_Label);
            HBox hBox2 = new HBox(bestPlayerNameLabel, new Separator(Orientation.VERTICAL), bestPlayerScoreLabel);
            VBox vBox = new VBox(hBox1, new Separator(), hBox2);
            vBox.setLayoutX(300);
            vBox.setLayoutY(50);
            group.getChildren().add(vBox);
        });
    }

    private void Fix_Position() {
        if (cycle <= 0)
            return;

        Head.setCenterX(Head.getCenterX() + 7.5);
        Body.setX(Body.getX() + 7.5);
        Foot_Left.setX(Foot_Left.getX() + 7.5);
        Foot_Left.setRotate(0);
        Foot_Right.setX(Foot_Right.getX() + 7.5);
        Foot_Right.setRotate(0);

        --cycle;

        if ((feed.getX() - Body.getX() <= 15&& feed.getX() - Body.getX() >= 0&&
                feed.getY() - Body.getY() <= 15&& feed.getY() - Body.getY() >= 0)||
                (Body.getX() - feed.getX() <= 15&& Body.getX() - feed.getX() >= 0&&
                        Body.getY() - feed.getY() <= 15&& Body.getY() - feed.getY() >= 0)) {
            ++Score;
            Platform.runLater(() -> Score_Label.setText(String.valueOf(Score)));
            feed.setX(-20);
            Toolkit.getDefaultToolkit().beep();
            ++Cherry_Count;
            Platform.runLater(() -> Cherry_Count_Label.setText(String.valueOf(Cherry_Count)));
        }
    }

    private void Cycle1_go() {
        if (cycle <= 0|| isEnd)
            return;

        Head.setCenterX(Head.getCenterX() + 7.5);
        Body.setX(Body.getX() + 7.5);
        Foot_Left.setX(Foot_Left.getX() + 7.5);
        if (Head.getCenterY() < Rectangle1.getY())
            Foot_Left.setRotate(20);
        else Foot_Left.setRotate(-20);
        Foot_Right.setX(Foot_Right.getX() + 7.5);
        if (Head.getCenterY() < Rectangle1.getY())
            Foot_Right.setRotate(-20);
        else Foot_Right.setRotate(20);
    }

    private void GameOver() {
        scene.setOnMousePressed(null);
        scene.setOnKeyPressed(null);

        PathTransition pathTransition1 = new PathTransition();
        FadeTransition fadeTransition1 = new FadeTransition();
        PathTransition pathTransition2 = new PathTransition();
        FadeTransition fadeTransition2 = new FadeTransition();
        PathTransition pathTransition3 = new PathTransition();
        FadeTransition fadeTransition3 = new FadeTransition();
        PathTransition pathTransition4 = new PathTransition();
        FadeTransition fadeTransition4 = new FadeTransition();

        Path path1 = new Path();
        Path path2 = new Path();
        Path path3 = new Path();
        Path path4 = new Path();

        path1.getElements().add(new MoveTo(Head.getCenterX(), Head.getCenterY() - 5));
        path1.getElements().add(new LineTo(Head.getCenterX(), 700));

        path2.getElements().add(new MoveTo(Body.getX() + 7.5, Body.getY()));
        path2.getElements().add(new LineTo(Body.getX(), 700));

        path3.getElements().add(new MoveTo(Foot_Left.getX(), Foot_Left.getY()));
        path3.getElements().add(new LineTo(Foot_Left.getX(), 700));

        path4.getElements().add(new MoveTo(Foot_Right.getX(), Foot_Right.getY()));
        path4.getElements().add(new LineTo(Foot_Right.getX(), 700));

        pathTransition1.setPath(path1);
        pathTransition2.setPath(path2);
        pathTransition3.setPath(path3);
        pathTransition4.setPath(path4);

        pathTransition1.setNode(Head);
        pathTransition2.setNode(Body);
        pathTransition3.setNode(Foot_Left);
        pathTransition4.setNode(Foot_Right);

        pathTransition1.setDuration(Duration.seconds(1));
        pathTransition2.setDuration(Duration.seconds(1));
        pathTransition3.setDuration(Duration.seconds(1));
        pathTransition4.setDuration(Duration.seconds(1));

        fadeTransition1.setNode(Head);
        fadeTransition2.setNode(Body);
        fadeTransition3.setNode(Foot_Left);
        fadeTransition4.setNode(Foot_Right);

        fadeTransition1.setFromValue(1);
        fadeTransition2.setFromValue(1);
        fadeTransition3.setFromValue(1);
        fadeTransition4.setFromValue(1);

        fadeTransition1.setToValue(0.3);
        fadeTransition2.setToValue(0.3);
        fadeTransition3.setToValue(0.3);
        fadeTransition4.setToValue(0.3);

        pathTransition1.play();
        pathTransition2.play();
        pathTransition3.play();
        pathTransition4.play();

        fadeTransition1.play();
        fadeTransition2.play();
        fadeTransition3.play();
        fadeTransition4.play();
        Tell_Observers();
    }

    private void Tell_Observers() {
        for (GameObserver observer : observers) {
            observer.update(Score, isEnd, Player_Name, Record, bestPlayerName);
        }
    }

    private void Restart_Game() {
//        group.getChildren().remove();
        Score = 0;
        Cherry_Count = 0;
        isEnd = false;
        adjustment = false;

        // Reset labels
        Platform.runLater(() -> {
            Score_Label.setText(String.valueOf(Score));
            Cherry_Count_Label.setText(String.valueOf(Cherry_Count));
        });

        // Reset button visibility
        Platform.runLater(() -> restartButton.setVisible(false));

        // Remove record display
        group.getChildren().removeIf(node -> node instanceof VBox);
        restartButton.setVisible(false);

        stick.setStartX(Rectangle1.getWidth());
        stick.setEndX(Rectangle1.getWidth());
        stick.setStartY(660 - Rectangle1.getHeight());
        stick.setEndY(660 - Rectangle1.getHeight());
        feed.setX(-20);

        // Reset game state
        Platform.runLater(() -> {
            StickHero newGame = new StickHero(scene, group, Player_Name);
            ((Group) scene.getRoot()).getChildren().add(newGame.getGroup());
        });

        // Close the old instance of the game
        Platform.runLater(() -> {
            ((Group) scene.getRoot()).getChildren().remove(group);
        });
    }

    private void Save_Record() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(
                    "files/best.txt", true)); // you may need to change this

            bw.write(Player_Name);
            bw.newLine();
            bw.write(String.valueOf(Score));
            bw.newLine();

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Set_Randoms() {
        int width = Math.abs(random.nextInt()) % 100 + 100;
        int height = 250;
        distance = Math.abs(random.nextInt()) % 200 + 100;

        Rectangle1.setWidth(width);
        Rectangle1.setHeight(height);
        Rectangle2.setWidth(width);
        Rectangle2.setHeight(height);

        Rectangle1.setY(660 - height);
        Rectangle2.setY(660 - height);
        Rectangle2.setX(width + distance);

        if (Score % 3 != 0)
            feed.setX(-20);

        stick.setStartX(width);
        stick.setEndX(width);
        stick.setStartY(660 - height);
        stick.setEndY(660 - height);

        Foot_Left.setX((double) width / 2);
        Foot_Right.setX((double) width / 2 + 10);
        Body.setX((double) width / 2);
        Head.setCenterX((double) width / 2 + 7.5);

        Foot_Left.setY(660 - height - 25);
        Foot_Right.setY(660 - height - 25);
        Body.setY(660 - height - 25 - 20);
        Head.setCenterY(660 - height - 25 - 20 - 5);
    }

    private void Set_Command_Adjustment() {
        scene.setOnMousePressed(e -> {
            adjustment = false;

            Thread thread = new Thread(() -> {

                if (stick.getEndX() - stick.getStartX() == 0) {
                    while (!adjustment) {
                        if (e.getButton() == MouseButton.PRIMARY)
                            stick.setEndY(stick.getEndY() - 4);
                        else stick.setEndY(stick.getEndY() + 4);

                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });

            thread.start();
        });

        scene.setOnMouseReleased(e -> adjustment = true);

        scene.setOnKeyPressed(e -> {
            Thread thread = new Thread(() -> {
                if (e.getCode().equals(KeyCode.F) && stick.getEndY() != stick.getStartY()) {
                    stick.setEndX(stick.getEndX() + stick.getStartY() - stick.getEndY());
                    stick.setEndY(stick.getStartY());

                    int feedX = Math.abs(random.nextInt()) % (distance - 100) + 50;
                    if (Score % 3 == 0) {
                        feed.setX(feedX + Rectangle1.getWidth());
                        if (random.nextInt() > 0)
                            feed.setY(Rectangle1.getY() + 40);
                        else feed.setY(Rectangle1.getY() - 40);
                    }

                    Play_Game();
                    Set_BestPlayer();
                }

                if (e.getCode().equals(KeyCode.I)&& stick.getEndY() == stick.getStartY()) {
                    Move_Inverse();
                }
            });
            thread.start();
        });


    }

    private void Move_Inverse() {
        if (Head.getCenterY() < Rectangle1.getY()) {
            Head.setCenterY(Head.getCenterY() + 2 * (5 + 20 + 25));
            Body.setY(Body.getY() + 20 + 25 + 25);
            Foot_Left.setY(Foot_Left.getY() + 25);
            Foot_Right.setY(Foot_Right.getY() + 25);
        }

        else if (Head.getCenterY() > Rectangle1.getY()) {
            Head.setCenterY(Head.getCenterY() - 2 * (5 + 20 + 25));
            Body.setY(Body.getY() - 20 - 25 - 25);
            Foot_Left.setY(Foot_Left.getY() - 25);
            Foot_Right.setY(Foot_Right.getY() - 25);
        }
    }

    @Override
    public void update(int score, boolean isEnd, String playerName, int bestRecord, String bestPlayerName) {

    }
}

