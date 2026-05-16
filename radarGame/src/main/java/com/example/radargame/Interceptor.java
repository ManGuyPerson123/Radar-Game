package com.example.radargame;

public class Interceptor {
    private double x;
    private double y;
    private double targetx;
    private double targety;
    private double speed = 1.0;
    private boolean boom = false;

    public Interceptor(double x, double y, double tx, double ty){
        this.x = x;
        this.y = y;
        this.targetx = tx;
        this.targety = ty;
    }

    public void updatePos(){
        double dx = targetx-this.x;
        double dy = targety-this.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        if(dist>speed){
            this.x += ((dx/dist)* speed);
            this.y += ((dy/dist)* speed);
        }else{
            this.x = targetx;
            this.y = targety;
            boom = true;
        }
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public boolean isBoom(){
        return this.boom;
    }

}
