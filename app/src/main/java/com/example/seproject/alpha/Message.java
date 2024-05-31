package com.example.seproject.alpha;

public class Message {
    private int num;
    private String txt;

    public Message(){}

    public Message(int num, String txt){
        this.num = num;
        this.txt = txt;
    }
    public String toString(){
        return txt;
    }
    public int getNum() {
        return num;
    }
    public String getTxt(){
        return txt;
    }
}
