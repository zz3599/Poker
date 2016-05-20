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
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.STRAIGHT_FLUSH);
		//111 1100
		Assert.assertTrue(classification.getCardRank() == 0x7C); 
		//1 1000 0000
		Assert.assertTrue(classification.getKickerRank() == 0x180); 
	}
	
	@Test
	public void TestFourOfAKind(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 3),
				new Card(Card.Suite.CLUB, 3),
				new Card(Card.Suite.HEARTS, 4), 
				new Card(Card.Suite.SPADE, 4),				
				new Card(Card.Suite.CLUB, 4)
		};		
		Hand hand = new Hand(new Card(Card.Suite.DIAMOND, 3), new Card(Card.Suite.SPADE, 3));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.FOUR_KIND);
		// 1000
		Assert.assertTrue(classification.getCardRank()  == 0x8); 
		// 1 0000
		Assert.assertTrue(classification.getKickerRank() == 0x10); 
	}
	
	@Test
	public void TestFullHouse(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 3),
				new Card(Card.Suite.CLUB, 7),
				new Card(Card.Suite.HEARTS, 4), 
				new Card(Card.Suite.SPADE, 4),				
				new Card(Card.Suite.CLUB, 8)
		};
		Hand hand = new Hand(new Card(Card.Suite.DIAMOND, 3), new Card(Card.Suite.CLUB, 4));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.FULL_HOUSE);
		// 1 1000
		Assert.assertTrue(classification.getCardRank()  == 0x18); 
		// 1 1000 0000
		Assert.assertTrue(classification.getKickerRank() == 0x180); 		
	}
	
	@Test
	public void TestFlush(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 3),
				new Card(Card.Suite.HEARTS, 4),
				new Card(Card.Suite.HEARTS, 7), 
				new Card(Card.Suite.SPADE, 10),				
				new Card(Card.Suite.CLUB, 14)
		};
		Hand hand = new Hand(new Card(Card.Suite.HEARTS, 11), new Card(Card.Suite.HEARTS, 14));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.FLUSH);
		// 100 1000 1001 1000
		Assert.assertTrue(classification.getCardRank()  == 0x4898); 
		// 100 0100 0000 0000
		Assert.assertTrue(classification.getKickerRank() == 0x4400); 	
	}
	
	@Test
	public void TestStraight(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 10),				 
				new Card(Card.Suite.SPADE, 13),				
				new Card(Card.Suite.CLUB, 14),
				new Card(Card.Suite.HEARTS, 14),
				new Card(Card.Suite.DIAMOND, 14)
		};
		Hand hand = new Hand(new Card(Card.Suite.HEARTS, 11), new Card(Card.Suite.DIAMOND, 12));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.STRAIGHT);
		// 111 1100 0000 0000
		Assert.assertTrue(classification.getCardRank()  == 0x7C00); 
		// 100 0000 0000 0000
		Assert.assertTrue(classification.getKickerRank() == 0x4000);
	}
	
	@Test
	public void TestThreeOfKind(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 2),				 
				new Card(Card.Suite.SPADE, 3),				
				new Card(Card.Suite.CLUB, 4),
				new Card(Card.Suite.HEARTS, 7),
				new Card(Card.Suite.DIAMOND, 8)
		};
		Hand hand = new Hand(new Card(Card.Suite.DIAMOND, 2), new Card(Card.Suite.SPADE, 2));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.THREE_KIND);
		// 00 0000 0100
		Assert.assertTrue(classification.getCardRank()  == 0x4); 
		// 0001 1001 1000
		Assert.assertTrue(classification.getKickerRank() == 0x198);
	}
	
	@Test
	public void TestTwoPair(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 2),				 
				new Card(Card.Suite.SPADE, 3),				
				new Card(Card.Suite.CLUB, 4),
				new Card(Card.Suite.HEARTS, 7),
				new Card(Card.Suite.DIAMOND, 8)
		};
		Hand hand = new Hand(new Card(Card.Suite.DIAMOND, 2), new Card(Card.Suite.HEARTS, 8));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.TWO_PAIR);
		// 1 0000 0100
		Assert.assertTrue(classification.getCardRank()  == 0x104); 
		// 1001 1000
		Assert.assertTrue(classification.getKickerRank() == 0x98);
	}
	
	@Test
	public void TestOnePair(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 2),				 
				new Card(Card.Suite.SPADE, 3),				
				new Card(Card.Suite.CLUB, 4),
				new Card(Card.Suite.HEARTS, 7),
				new Card(Card.Suite.DIAMOND, 8)
		};
		Hand hand = new Hand(new Card(Card.Suite.DIAMOND, 2), new Card(Card.Suite.HEARTS, 14));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.PAIR);
		// 0100
		Assert.assertTrue(classification.getCardRank()  == 0x4); 
		// 100 0001 1001 1000
		Assert.assertTrue(classification.getKickerRank() == 0x4198);
	}
	
	@Test
	public void TestHighCard(){
		Card[] communityCards = new Card[]{
				new Card(Card.Suite.HEARTS, 2),				 
				new Card(Card.Suite.SPADE, 3),				
				new Card(Card.Suite.CLUB, 4),
				new Card(Card.Suite.HEARTS, 7),
				new Card(Card.Suite.DIAMOND, 8)
		};
		Hand hand = new Hand(new Card(Card.Suite.DIAMOND, 13), new Card(Card.Suite.HEARTS, 14));
		HandClassification classification = classifier.getHandClassification(Arrays.asList(communityCards), hand);
		Assert.assertTrue(classification.getHandRank() == HandRank.HIGH_CARD);
		// 0
		Assert.assertTrue(classification.getCardRank()  == 0x0); 
		// 110 0001 1001 1100
		Assert.assertTrue(classification.getKickerRank() == 0x619C);
	}
}
