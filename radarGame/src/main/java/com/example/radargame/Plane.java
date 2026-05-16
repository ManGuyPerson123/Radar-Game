package com.example.radargame;

public class Plane {
    private double x;
    private double y;

    public Plane(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getPlaneDist(int centerx, int centery){
        double dx = this.x-centerx;
        double dy = this.y-centery;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public void updatePos(){
        double centerX = 320.0;
        double centerY = 240.0;
        double dx = centerX-this.x;
        double dy = centerY-this.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        if(dist>1){
            double speed = 0.05;
            this.x += ((dx/dist)* speed);
            this.y += ((dy/dist)* speed);
        }
        if(dist<=1){
            System.out.println("BASE DESTROYED");
        }
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }
}
