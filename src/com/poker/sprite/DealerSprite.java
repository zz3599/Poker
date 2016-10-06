package com.poker.sprite;


public class DealerSprite extends TablePositionSprite {
	private static final String DEALER_URL = "res/board/dealer.png";

	public DealerSprite(int tablePosition) {
		super(tablePosition);
	}

	public void setTablePosition(int tablePosition) {
		this.tablePosition = tablePosition;
	}

	@Override
	public String getImageURL() {
		return DEALER_URL;
	}
}
