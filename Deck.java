import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

//models a deck of 52 standard playing cards, each with 21 Blackjack values
//Ace = 11 by default, 1 in the case of a bust
//Jack, Queen, King = 10
//All other cards have value shown
public class Deck{
   private ArrayList<Card> cards;
   
   //fills the deck of cards with 21 Blackjack values
   public Deck(){
      cards = new ArrayList<Card>();
      for (int y = 0; y < 4; y++){
         for (int x = 0; x < 13; x++){
            int value;
            if (x == 0){
            value = 11;
            }
            else if (x >= 10)
            {
               value = 10;
            }
            else{
               value = x + 1;
            }
            cards.add(new Card(value, x * 72, y * 96));
         }
      }
      shuffle();
   }
   
   //shuffles the deck
   public void shuffle(){
      Collections.shuffle(cards);
   }
   
   //removes the top card from the deck and passes it to a hand
   public void passCardTo(Hand recipient){
      recipient.takeCard(cards.remove(0));
   }
   
   //receives a card from a hand
   public void getCardsFrom(Hand source){
      while(source.hasCards()){
         cards.add(source.returnCard());
      }
   }
}