import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

/*
   Describes the behavior of a single 21 Blackjack card. Handles special cases for aces.
*/

public class Card{
   private int value;
   private BufferedImage picture;
 
   //Sets the value, screen location, and picture for the card
   public Card(int value, int x, int y){
      this.value = value;
      try{
         picture = ImageIO.read(new File("cards.png")).getSubimage(x, y, 72, 96);
      }
      catch(IOException e){}
   }
   
   //Brings ace's value from 11 to 1
   public void decreaseAce(){
      value = 1;
   }
   
   //Brings ace's value from 1 to 11
   public void increaseAce(){
      value = 11;
   }
   
   //Paints the card picture at the specified location
   public void paint(Graphics g, int x, int y){
      g.drawImage(picture, x, y, null);
   }
   
   public int getValue(){
      return value;
   }
}