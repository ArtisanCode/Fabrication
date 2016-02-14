package com.artisancode.fabrication;

public class Fabricator<T>
{
	public JObjectBuilder<T> createNew(Class<T> target){
		return new JObjectBuilder<T>(target);
	}
}
