package test.poker.hand;

import org.junit.Assert;
import org.junit.Test;

import com.poker.hand.Hand;
import com.poker.lib.Card;
import com.poker.lib.Suite;

public class TestHand {
	@Test
	public void TestSizeConstructor(){
		Hand hand = new Hand(2);
		Assert.assertEquals(hand.getHandSize(), Hand.POKER_HAND_SIZE);
		Assert.assertEquals(hand.getCards().size(), 0);
	}
	
	@Test
	public void TestCardsArrayConstructor(){
		Hand hand = new Hand(new Card[]{new Card(Suite.DIAMOND, 2), new Card(Suite.CLUB, 4)});
		Assert.assertEquals(hand.getHandSize(), Hand.POKER_HAND_SIZE);
		Assert.assertEquals(hand.getCards().size(), Hand.POKER_HAND_SIZE);
	}
}
