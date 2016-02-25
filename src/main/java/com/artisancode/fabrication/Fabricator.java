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

	public CollectionBuilder<T> createNewCollection(Class<T> target)
	{
		return new CollectionBuilder<T>(target, new FabricatorConfiguration());
	}

	public CollectionBuilder<T> createNewCollection(Class<T> target, FabricatorConfiguration configuration)
	{
		return new CollectionBuilder<T>(target, configuration);
	}
}
