import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.util.*;

/*
   Notes to self:
      Cards are 72 x 96.
*/

public class Blackjack extends JPanel implements MouseListener{
   private Deck deck;   //Deck of 52 standard cards
   private Hand playerHand; //Holds the player's cards
   private Hand dealerHand; //Holds the dealer's cards
   private static JFrame mainFrame; //the main window for the game. Later subwindows will be included to indicate player info, winner, and snakes and ladder locations
   private File yourFile; //sound files
   private AudioInputStream stream;
   private AudioFormat format;
   private DataLine.Info info;
   private Clip clip;
   private javax.swing.Timer dealerTimer; //allows for delayed flipping of the dealer's cards
   private boolean dealerBust, playerBust, playerBlackjack, dealerBlackjack, playerStand;
   private int wins, losses, push; //counters for each ending scenario
   
   public Blackjack(){
      deck = new Deck();
      playerHand = new Hand(100);
      dealerHand = new Hand(300);
      dealerTimer = new javax.swing.Timer(500, new DealerTimer()); //dealer hits every 1/2 second
      deck.passCardTo(playerHand); //moves top 2 cards from the deck to the player hand
      deck.passCardTo(playerHand);
      playerBlackjack = playerHand.getValue() == 21; 
      playerStand = playerHand.getValue() == 21; //if player got blackjack, they should not hit
      deck.passCardTo(dealerHand); //gives one card to the dealer
      addMouseListener(this);
      repaint(); //updates the state of the board
   }
   
   public void reset(){
      deck.getCardsFrom(playerHand); //return cards from hands to deck
      deck.getCardsFrom(dealerHand);
      deck.shuffle();
      deck.passCardTo(playerHand); //moves top 2 cards from the deck to the player hand
      deck.passCardTo(playerHand);
      playerBlackjack = playerHand.getValue() == 21;
      playerStand = playerHand.getValue() == 21;
      deck.passCardTo(dealerHand); //gives one card to the dealer
      dealerBust = playerBust = dealerBlackjack = false;
      repaint();
   }
   
   //invoked when the repaint() method is called
   public void paintComponent(Graphics g){
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setFont(new Font("Arial Black", Font.BOLD, 40));
      g.setColor(Color.BLACK);
      g.drawString("Blackjack!", 100, 50);
      g.setFont(new Font("Arial Black", Font.BOLD, 20));
      g.drawString("by Kim Baram", 100, 90);
      playerHand.paint(g);
      g.drawString("Your hand: " + playerHand.getValue(), playerHand.getX(), 120);
      if (playerBlackjack){
         g.setColor(Color.GREEN.darker());
         g.drawString("Blackjack!", playerHand.getX(), 150);
         g.setColor(Color.BLACK);
      }
      else if (playerBust){
         g.setColor(Color.RED.darker());
         g.drawString("Busted!", playerHand.getX(), 150);
         g.setColor(Color.BLACK);
      }
      dealerHand.paint(g);
      g.drawString("Dealer hand: " + dealerHand.getValue(), dealerHand.getX(), 320);
      if (dealerBlackjack){
         g.setColor(Color.GREEN.darker());
         g.drawString("Blackjack!", dealerHand.getX(), 350);
         g.setColor(Color.BLACK);
      }
      else if (dealerBust){
         g.setColor(Color.RED.darker());
         g.drawString("Busted!", dealerHand.getX(), 350);
         g.setColor(Color.BLACK);
      }
      playerHand.resetX();
      dealerHand.resetX();
      if (!playerStand){  //only allow hit and stand options when player is not already standing
         g.fillRect(1100, 50, 150, 50);
         g.setColor(Color.WHITE);
         g.setFont(new Font("Arial Black", Font.PLAIN, 30));
         g.drawString("Hit", 1150, 80);
         g.setColor(Color.BLACK);
         g.fillRect(1100, 150, 150, 50);
         g.setColor(Color.WHITE);
         g.setFont(new Font("Arial Black", Font.PLAIN, 30));
         g.drawString("Stand", 1125, 180);  
         g.setColor(Color.BLACK);
      }
      else if (!dealerTimer.isRunning()){ //only allow to deal after game ends
         g.fillRect(1100, 250, 150, 50);
         g.setColor(Color.WHITE);
         g.setFont(new Font("Arial Black", Font.PLAIN, 30));
         g.drawString("Deal", 1130, 280);
      }
      g.setColor(Color.BLACK);
      g.setFont(new Font("Arial Black", Font.PLAIN, 40));
      g.drawString("Wins: " + wins + ", Losses: " + losses + ", Pushes: " + push, 100, 500);
   }
   
      //method required for mouse listener interface
   public void mousePressed(MouseEvent e) {
   
   }

   //method required for mouse listener interface
   public void mouseReleased(MouseEvent e) {
   
   }

   //method required for mouse listener interface
   public void mouseEntered(MouseEvent e) {
   
   }

   //method required for mouse listener interface
   public void mouseExited(MouseEvent e) {
   
   }

   //method required for mouse listener interface, handles the clicking of the buttons
   public void mouseClicked(MouseEvent e) {
      int x = e.getX();  //get location of the mouse click
      int y = e.getY();
      if (!playerStand && x >= 1100 && x <= 1250 && y >= 50 && y <= 100){  //hit button clicked
         deck.passCardTo(playerHand);
         playerBlackjack = playerHand.getValue() == 21;
         playerBust = playerHand.getValue() > 21;
         if (playerBust){
            try { //play fanfare music https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java
               try{
                  clip.stop();
               }
               catch(Exception ex){
               }
               yourFile = new File("bust.wav");
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
            playerStand = true;
            dealerTimer.start();
         }
         if(playerBlackjack){
            playerStand = true;
            dealerTimer.start(); //TODO: why does dealer "quit" if player gets Blackjack?
         }
      }
      else if (!playerStand && x >= 1100 && x <= 1250 && y >= 150 && y <= 200){  //stand button clicked
         playerStand = true;
         dealerTimer.start();
      }
      else if (playerStand && !dealerTimer.isRunning() && x >= 1100 && x <= 1250 && y >= 250 && y <= 300){  //deal button clicked
         reset();
      }
      repaint();
   }

//triggers every half second to flip dealer cards
   private class DealerTimer implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if (dealerHand.getValue() < 17 && !playerBust){ //dealer stands at 17, or automatically stands when player busts
            deck.passCardTo(dealerHand);
            dealerBlackjack = dealerHand.getValue() == 21;
            dealerBust = dealerHand.getValue() > 21;
         }
         else{ //stop dealing cards and determine winner
            dealerTimer.stop();
            if (playerBust){
               losses++;
            }
            else if (dealerBust){
               wins++;
               try{
                  try{
                     clip.stop();
                  }
                  catch(Exception ex){
                  }
                  yourFile = new File("win.wav");
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
            }
            else if (playerHand.getValue() == dealerHand.getValue()){
               push++;
               try{
                  try{
                     clip.stop();
                  }
                  catch(Exception ex){
                  }
                  yourFile = new File("push.wav");
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
            }
            else if (playerHand.getValue() > dealerHand.getValue()){
               wins++;
               try{
                  try{
                     clip.stop();
                  }
                  catch(Exception ex){
                  }
                  yourFile = new File("win.wav");
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
            }
            else{
               losses++;
               try{
                  try{
                     clip.stop();
                  }
                  catch(Exception ex){
                  }
                  yourFile = new File("lose.wav");
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

            }
         }
         repaint();
      }
   }
   
   //Builds the main frame and sets the panel contents. Will be modified to ask for player info first.
   public static void main(String[] args){
      mainFrame = new JFrame("Blackjack!");
      mainFrame.setSize(1400, 600);
      mainFrame.setLocation(0, 20);
      mainFrame.setContentPane(new Blackjack());
      mainFrame.setVisible(true);
      mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mainFrame.setResizable(false);
   }
}