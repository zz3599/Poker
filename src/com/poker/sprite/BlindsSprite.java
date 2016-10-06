package com.poker.sprite;


public class BlindsSprite extends TablePositionSprite {
	protected static final String BIG_BLIND_URL = "res/board/chips/chip_bigblind_top.png";
	protected static final String SMALL_BLIND_URL = "res/board/chips/chip_smallblind_top.png";

	protected boolean isBigBlind;
	protected int blindAmount;

	public BlindsSprite(int tablePosition, boolean isBigBlind, int blindAmount) {
		super(tablePosition);
		this.isBigBlind = isBigBlind;
		this.blindAmount = blindAmount;
	}

	@Override
	public String getImageURL() {
		return isBigBlind ? BIG_BLIND_URL : SMALL_BLIND_URL;
	}

	public boolean isBigBlind() {
		return isBigBlind;
	}

	public int getBlindAmount() {
		return blindAmount;
	}

}
