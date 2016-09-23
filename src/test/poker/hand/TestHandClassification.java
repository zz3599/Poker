package test.poker.hand;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.poker.hand.HandClassification;
import com.poker.hand.HandRank;
import com.poker.lib.Card;
import com.poker.lib.Suite;

public class TestHandClassification {
	private static final List<HandClassification> handClassifications = Arrays
			.asList(new HandClassification[] {
					new HandClassification(HandRank.STRAIGHT_FLUSH),
					new HandClassification(HandRank.FOUR_KIND),
					new HandClassification(HandRank.FULL_HOUSE),
					new HandClassification(HandRank.FLUSH),
					new HandClassification(HandRank.STRAIGHT),
					new HandClassification(HandRank.THREE_KIND),
					new HandClassification(HandRank.TWO_PAIR),
					new HandClassification(HandRank.PAIR),
					new HandClassification(HandRank.HIGH_CARD), });

	@Test
	public void TestHandClassificationConstructor1Arg() {
		HandClassification class1 = new HandClassification(HandRank.FLUSH);
		Assert.assertEquals(class1.getHandRank(), HandRank.FLUSH);
		Assert.assertEquals(class1.getCardValues().size(), 0);
		Assert.assertEquals(class1.getCardKickers().size(), 0);
	}

	@Test
	public void TestHandClassificationComparisonDifferentRanks() {
		// successively test higher ranks with all their lower ranks, and
		// confirm that we get a positive value
		// successively test lower ranks with their higher ranks, and confirm
		// that we get a negative value
		// finally, test with themselves, to confirm that we get 0
		for (int i = 0; i < handClassifications.size(); i++) {
			HandClassification testClassification = handClassifications.get(i);

			for (int j = i + 1; j < handClassifications.size(); j++) {
				Assert.assertTrue(testClassification
						.compareTo(handClassifications.get(j)) > 0);
			}
			for (int j = i - 1; j >= 0; j--) {
				Assert.assertTrue(testClassification
						.compareTo(handClassifications.get(j)) < 0);
			}
			Assert.assertTrue(testClassification.compareTo(testClassification) == 0);
		}
	}

	@Test
	public void TestHandClassificationComparisonSameRanksDifferentValue() {
		// Different cardValue
		HandClassification class1 = new HandClassification(HandRank.PAIR,
				Arrays.asList(new Card[] { new Card(Suite.CLUB, 4),
						new Card(Suite.HEARTS, 4) }));
		HandClassification class2 = new HandClassification(HandRank.PAIR,
				Arrays.asList(new Card[] { new Card(Suite.CLUB, 5),
						new Card(Suite.HEARTS, 5) }));
		// 4 < 5;
		System.out.println(class1.compareTo(class2));
		Assert.assertTrue(class1.compareTo(class2) < 0);
	}

	@Test
	public void TestHandClassificationComparisonSameRanksSameValueDifferentKicker() {
		// Same cardValue, different kickers
		HandClassification class1 = new HandClassification(HandRank.PAIR,
				Arrays.asList(new Card[] { new Card(Suite.CLUB, 4),
						new Card(Suite.HEARTS, 4) }),
				Arrays.asList(new Card[] { new Card(Suite.CLUB, 14), new Card(Suite.CLUB, 13) }));
		HandClassification class2 = new HandClassification(HandRank.PAIR,
				Arrays.asList(new Card[] { new Card(Suite.CLUB, 4),
						new Card(Suite.HEARTS, 4) }),
				Arrays.asList(new Card[] {new Card(Suite.CLUB, 14), new Card(Suite.CLUB, 12)}));
		// 13 > 12		
		Assert.assertTrue(class1.compareTo(class2) > 0);
	}
}
