package com.example.radargame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static javafx.scene.text.Font.font;

public class GameApp extends Application {
    private double radarSweepAngle = 0.0;
    private GraphicsContext mainGC;
    private int centerX;
    private int centerY;
    private ArrayList<Plane> planeList = new ArrayList<Plane>();
    private ArrayList<Marker> markList = new ArrayList<Marker>();
    private ArrayList<Interceptor> interceptors = new ArrayList<Interceptor>();
    private int activeTime = 0;
    private int activePlanes = 0;
    private int markX = 580;
    private int markY = 180;
    private int markSpacing = 25;

    @Override
    public void start(Stage stage) throws IOException {
        Canvas gameCanvas = new Canvas(640.0,480.0);
        mainGC = gameCanvas.getGraphicsContext2D();
        centerX = (int)gameCanvas.getWidth()/2;
        centerY = (int)gameCanvas.getHeight()/2;
        Pane mainPane = new Pane(gameCanvas);
        Scene mainScene = new Scene(mainPane,640,480,Color.BLACK);
        stage.setTitle("Radar Display");
        stage.setScene(mainScene);
        stage.show();

        AnimationTimer mainTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                radarSweepAngle += 1.5;
                drawScreen();
            }
        };
        mainTimer.start();
        gameCanvas.setOnMouseClicked(e->{
            double mouseX = e.getX();
            double mouseY = e.getY();
            boolean clickExisting = false;
            for(int i=0;i<markList.size();i++){
                Marker m = markList.get(i);
                double dxm = mouseX - m.getX();
                double dym = mouseY - m.getY();
                double distm = Math.sqrt(dxm * dxm + dym * dym);
                if(distm<15){
                    markList.remove(i);
                    clickExisting = true;
                    break;
                }
            }
            double dxp = centerX-mouseX;
            double dyp = centerY-mouseY;
            double distp = Math.sqrt(dxp*dxp + dyp*dyp);
            if(!clickExisting){
                if(markList.size()>=5){
                    markList.remove(0);
                }
                if(distp<200) {
                    markList.add(new Marker(mouseX, mouseY));
                }
            }
        });
        VBox userInput = new VBox(5.0);
        userInput.setLayoutX(10);
        userInput.setLayoutY(380);
        TextField angleInput = new TextField();
        angleInput.setPromptText("Angle: (0-359)");
        TextField distInput = new TextField();
        distInput.setPromptText("Distance: (0-40km)");
        Button launchBtn = new Button("LAUNCH");
        launchBtn.setOnAction(e->{
            double angle = Double.parseDouble(angleInput.getText());
            double km = Double.parseDouble(distInput.getText());
            double pixDist = km*10;
            double tx = centerX + Math.cos(Math.toRadians(angle))*pixDist;
            double ty = centerY - Math.sin(Math.toRadians(angle))*pixDist;
            interceptors.add(new Interceptor(centerX,centerY,tx,ty));
            angleInput.setText("");
            distInput.setText("");
        });
        userInput.getChildren().addAll(angleInput, distInput, launchBtn);
        mainPane.getChildren().add(userInput);
    }
    private void drawScreen(){
        mainGC.setFill(Color.rgb(0,0,0,0.1));
        mainGC.fillRect(0, 0, 640, 480);
        mainGC.setFill(Color.LIMEGREEN);
        mainGC.fillOval(centerX-5,centerY-5,10,10);
        mainGC.setStroke(Color.LIMEGREEN);
        for(int i=1;i<5;i++){
            double w = i*100;
            double h = i*100;
            mainGC.strokeOval(centerX-(w/2),centerY-(h/2),w,h);
            int km = i*10;
            mainGC.setFont(font("Lucida Console",9));
            mainGC.fillText(km+" km",centerX-13,centerY-(h/2)+15);

        }
        mainGC.fillText("0°",centerX+210,centerY+5);
        mainGC.fillText("90°",centerX-5,centerY-205);
        mainGC.fillText("180°",centerX-230,centerY+5);
        mainGC.fillText("270°",centerX-5,centerY+215);
        mainGC.setFont(font("Lucida Console",18));
        mainGC.fillText("THREATS DETECTED: "+activePlanes,centerX-97,25);

        double radius = 200;
        double endX = centerX + Math.cos(Math.toRadians(radarSweepAngle)) * radius;
        double endY = centerY + Math.sin(Math.toRadians(radarSweepAngle)) * radius;
        mainGC.strokeLine(centerX, centerY, endX, endY);
        activeTime++;
        for(Marker m:markList){
            mainGC.setFill(Color.RED);
            mainGC.fillOval(m.getX()-4,m.getY()-4,8,8);
        }
        for(int i=0;i<5;i++){
            if(i<(5-markList.size())){
                mainGC.setFill(Color.RED);
                mainGC.fillOval(markX,markY+(i*markSpacing),20,20);
            }else{
                mainGC.setStroke(Color.DARKGRAY);
                mainGC.strokeOval(markX,markY+(i*markSpacing),20,20);
            }
        }
        for(int i=interceptors.size()-1;i>=0;i--){
            if(interceptors.get(i).isBoom()){
                checkCollision(interceptors.get(i).getX(),interceptors.get(i).getY());
                mainGC.setStroke(Color.LIME);
                mainGC.strokeOval(interceptors.get(i).getX()-10,interceptors.get(i).getY()-10,20,20);
                interceptors.remove(i);
            }
            if(checkCollision(interceptors.get(i).getX(),interceptors.get(i).getY())){
                mainGC.setStroke(Color.LIME);
                mainGC.strokeOval(interceptors.get(i).getX()-10,interceptors.get(i).getY()-10,20,20);
                interceptors.remove(i);
            }else{
                checkCollision(interceptors.get(i).getX(),interceptors.get(i).getY());
                interceptors.get(i).updatePos();
                mainGC.setFill(Color.LIME);
                mainGC.fillOval(interceptors.get(i).getX()-2.5,interceptors.get(i).getY()-2.5,5,5);
            }
        }
        if(activeTime==0 || activeTime%1000==0){
            double randX = 0;
            double randY = 0;
            double distr = 0.0;
            while(distr<205){
                randX = (Math.random() * 640);
                randY = (Math.random() * 480);
                double dx = randX - centerX;
                double dy = randY - centerY;
                distr = Math.sqrt(dx * dx + dy * dy);
            }
            if(planeList.size()<=5) {
                planeList.add(new Plane(randX, randY));
                activePlanes++;
            }
        }
        for(Plane p:planeList) {
            p.updatePos();
            double dist = p.getPlaneDist(centerX, centerY);
            if (dist < 200) {
                double ratio = dist / 200;
                double checkX = centerX + (endX - centerX) * ratio;
                double checkY = centerY + (endY - centerY) * ratio;
                if (Math.sqrt(Math.pow(checkX - p.getX(), 2) + Math.pow(checkY - p.getY(), 2)) < 7) {
                    mainGC.setFill(Color.LIME);
                    mainGC.fillOval(p.getX() - 5, p.getY() - 5, 10, 10);
                }
            }
        }

    }
    public boolean checkCollision(double bx, double by){
        double bRadius = 50;
        for(int i=planeList.size()-1;i>=0;i--){
            double dx = planeList.get(i).getX()-bx;
            double dy = planeList.get(i).getY()-by;
            double dist = Math.sqrt(dx*dx + dy*dy);
            if(dist<bRadius){
                planeList.remove(i);
                System.out.println("TARGET NEUTRALIZED");
                return true;
            }
        }
        return false;
    }
}
