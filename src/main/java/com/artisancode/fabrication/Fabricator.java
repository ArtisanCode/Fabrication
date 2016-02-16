package com.artisancode.fabrication;

public class Fabricator<T>
{
	public JObjectBuilder<T> createNew(Class<T> target){
		return new JObjectBuilder<>(target, new FabricatorConfiguration());
	}

	public JObjectBuilder<T> createNew(Class<T> target, FabricatorConfiguration configuration)
	{
		return new JObjectBuilder<>(target, configuration);
	}
}
