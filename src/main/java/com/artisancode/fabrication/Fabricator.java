package com.artisancode.fabrication;

public class Fabricator<T>
{
	public ObjectBuilder<T> createNew(Class<T> target)
	{
		return new ObjectBuilder<>(target, new FabricatorConfiguration());
	}

	public ObjectBuilder<T> createNew(Class<T> target, FabricatorConfiguration configuration)
	{
		return new ObjectBuilder<>(target, configuration);
	}
}
