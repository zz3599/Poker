package com.poker.sprite;

import com.poker.lib.IRenderable;

/**
 * A renderable that has a table position.
 *
 */
public abstract class TablePositionSprite implements IRenderable {
	protected int tablePosition;

	protected TablePositionSprite(int tablePosition) {
		this.tablePosition = tablePosition;
	}

	public int getTablePosition() {
		return this.tablePosition;
	}

	public void setTablePosition(int tablePosition) {
		this.tablePosition = tablePosition;
	}
}
