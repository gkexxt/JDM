/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamultithread1;

/**
 *
 * @author gkalianan
 
public class Obs {
    
}*/

import java.util.Observable;
import java.util.Observer;

class MessageBoard extends Observable
{
    public void changeMessage(String message) 
    {
        setChanged();
        notifyObservers(message);
    }
}

class Student implements Observer 
{
    @Override
    public void update(Observable o, Object arg) 
    {
        System.out.println("Message board changed: " + arg);
    }
}

public class Obs 
{
    public static void main(String[] args) 
    {
        MessageBoard  board = new MessageBoard ();
        Student bob = new Student();
        Student joe = new Student();
        board.addObserver(bob);
        board.addObserver(joe);
        board.changeMessage("More Homework!");
    }
}