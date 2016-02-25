package com.artisancode.fabrication;

public class FabricationException extends RuntimeException
{
	public FabricationException(Exception e)
	{
		this.initCause(e);
	}
}
