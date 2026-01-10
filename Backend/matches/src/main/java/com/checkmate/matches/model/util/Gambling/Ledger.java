package com.checkmate.matches.model.util.Gambling;

import java.util.ArrayList;

public class Ledger
{
    public double pot;
    private double playerOneBets;
    private double playerTwoBets;

    private ArrayList<Bettor> gameLedger = new ArrayList<Bettor>();

    public Ledger()
    {
        pot = 0;
        playerOneBets = 0;
        playerTwoBets = 0;
    }

    public void addBettor(Bettor myBettor)
    {
        gameLedger.add(myBettor);
    }

    private void setPot()
    {
        for(int i = 0; i < gameLedger.size(); i++)
        {
            pot += gameLedger.get(i).betAmount;
        }
    }
}
