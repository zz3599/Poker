package com.engine;

public class EngineException extends Exception{
	public EngineException(String msg) {
		super(msg);
	}
	
	public EngineException(String msg, Throwable t) {
		super(msg, t);
	}
}
