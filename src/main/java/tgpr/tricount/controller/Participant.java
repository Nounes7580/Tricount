package tgpr.tricount.controller;

public class Participant  {

    private double balance;
    private int idParticipant;

    public Participant(int balance, int idParticipant) {
        this.balance = balance;
        this.idParticipant = idParticipant;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getIdParticipant() {
        return idParticipant;
    }

    public void setIdParticipant(int idParticipant) {
        this.idParticipant = idParticipant;
    }
}
