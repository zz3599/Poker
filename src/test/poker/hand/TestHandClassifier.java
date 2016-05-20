package test.poker.hand;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.poker.Card;
import com.poker.hand.Hand;
import com.poker.hand.HandClassification;
import com.poker.hand.HandClassifier;
import com.poker.hand.HandRank;

public class TestHandClassifier {
	HandClassifier classifier;

	@Before
	public void Setup() {
		classifier = new HandClassifier();
	}

	@Test
	public void TestStraightFlush(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 8),
				new Card(Card.Suite.CLUB, 6),
				new Card(Card.Suite.CLUB, 2), 
				new Card(Card.Suite.CLUB, 3),				
				new Card(Card.Suite.HEARTS, 7)
		};
		Hand hand = new Hand(new Card(Card.Suite.CLUB, 4), new Card(Card.Suite.CLUB, 5));
		HandClassification classification = classifier.getHandClassifications(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getRank() == HandRank.STRAIGHT_FLUSH); 
		Assert.assertTrue(classification.getCardRank() == 0x7C); //111 1100
		Assert.assertTrue(classification.getKickerRank() == 0x180); //1 1000 0000
	}
}
