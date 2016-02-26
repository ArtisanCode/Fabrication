package com.artisancode.fabrication;

public class FabricationException extends RuntimeException
{
	public FabricationException(Exception e)
	{
		super(e);
	}

	public FabricationException(String message)
	{
		super(message);
	}
}
