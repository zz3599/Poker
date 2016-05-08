package com.poker.exception;

public class PokerException extends Exception {
	ErrorCode err;
	
	public PokerException(ErrorCode err){
		this.err = err;
	}
	
	public PokerException(String msg, ErrorCode err){
		
		super(msg);
		this.err = err;
	}
}
