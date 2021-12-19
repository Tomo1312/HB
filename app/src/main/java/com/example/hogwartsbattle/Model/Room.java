package com.example.hogwartsbattle.Model;

public class Room {
    private  String startGame, locked, classroom, classroomBought, banished, library, playing;

    public Room() {
        this.startGame = "false";
        this.locked = "false";
        this.classroom = "";
        this.classroomBought = "";
        this.banished = "";
        this.library = "8";
        this.playing = "";
    }

    public String getStartGame() {
        return startGame;
    }

    public void setStartGame(String startGame) {
        this.startGame = startGame;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClassroomBought() {
        return classroomBought;
    }

    public void setClassroomBought(String classroomBought) {
        this.classroomBought = classroomBought;
    }

    public String getBanished() {
        return banished;
    }

    public void setBanished(String banished) {
        this.banished = banished;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getPlaying() {
        return playing;
    }

    public void setPlaying(String playing) {
        this.playing = playing;
    }
}
