import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.awt.event.*;
import javax.sound.sampled.*;
/*
   Contains the cards in one party's hand (player or dealer)
*/
public class Hand{
   private ArrayList<Card> cards = new ArrayList<Card>();
   private int y; 
   private int x;
   private File yourFile; //location of the sound file to be used
   private AudioInputStream stream;
   private AudioFormat format;
   private DataLine.Info info;
   private Clip clip;
   private static final int startingX = 100;
   
   //initializes the leftmost position for the hand.
   public Hand(int y){
      x = startingX;
      this.y = y;
   }
   
   //takes a card and adds it to this hand
   public void takeCard(Card c){
      try { //play fanfare music https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
         try{
            clip.stop();
         }
         catch(Exception ex){
         }
         yourFile = new File("flipcard.wav");
         stream = AudioSystem.getAudioInputStream(yourFile);
         format = stream.getFormat();
         info = new DataLine.Info(Clip.class, format);
         clip = (Clip) AudioSystem.getLine(info);
         clip.open(stream);
         clip.start();
      } catch (Exception exception) {
         System.out.println(exception);
            		// whatevers
      } //end play fanfare music
   
      cards.add(c);
   }
   
   //returns true if cards contains at least 1 element
   public boolean hasCards(){
      return cards.size() > 0;
   }
   
   //removes the top card from this hand and returns it to the class calling for it
   public Card returnCard(){
      if (cards.get(0).getValue() == 1){
         cards.get(0).increaseAce();
      }
      return cards.remove(0);
   }
   
   //if the hand contains an ace valued at 11, retuces its value to 1 and returns true
   //returns false otherwise
   private boolean hasAce(){
      for (Card c : cards){
         if (c.getValue() == 11){
            c.decreaseAce();
            return true;
         }
      }
      return false;
   }
   
   //calculates and returns the sum of the value in cards. If sum exceeds 21 and there
   //is at least one ace valued at 11, the ace value is reduced to 1.
   public int getValue(){
      int value = 0;
      for (Card c : cards){
         value += c.getValue();
      }
      while (value > 21 && hasAce()){
         value -= 10;
      }
      return value;
   }
   
   //sets x back to starting point for repainting
   public void resetX(){
      x = startingX;
   }
   
   public int getX(){
      return x;
   }
   
   //paints the cards in this hand
   public void paint(Graphics g){
      for (Card c : cards){
         c.paint(g, x, y);
         x += 100;
      }
   }
}