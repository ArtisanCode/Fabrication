package com.artisancode.fabrication;

import java.util.HashMap;

public class FabricatorConfiguration
{
	public HashMap<Class<?>, Func<Object>> generators;
	public boolean useFieldNameForString;
	public boolean recursive;
	public int recurseLimit;
	public int generationSeed;

	public FabricatorConfiguration()
	{
		this.generationSeed = 0;

		setDefaultValues();
		init();
	}

	public FabricatorConfiguration(int generationSeed)
	{
		this.generationSeed = generationSeed;

		setDefaultValues();
		init();
	}

	public void init()
	{
		generators = new HashMap<>();

		generators.put(int.class, () -> generationSeed);
		generators.put(double.class, () -> (double) generationSeed);
		generators.put(byte.class, () -> (byte) generationSeed);
		generators.put(short.class, () -> (short) generationSeed);
		generators.put(long.class, () -> (long) generationSeed);
		generators.put(float.class, () -> (float) generationSeed);
		generators.put(char.class, () -> (char) ('A' + generationSeed));
		generators.put(boolean.class, () -> false);
		generators.put(String.class, () -> Integer.toString(generationSeed));
	}

	public Object generate(Class<?> targetClass, String fieldName)
	{
		Func<Object> generator = generators.get(targetClass);

		if (targetClass == String.class && useFieldNameForString && fieldName != null)
		{
			// Special case for Strings that use the fieldName
			return fieldName;
		}

		if (generator != null)
		{
			// A default generator exists ... use it!
			return generator.func();
		}

		if (targetClass.isEnum())
		{
			Class<? extends Enum<?>> targetEnumClass = (Class<? extends Enum<?>>) targetClass;
			return targetEnumClass.getEnumConstants()[0];
		}

		return null;
	}

	public void setDefaultValues()
	{
		recursive = false;
		recurseLimit = 5;
		useFieldNameForString = true;
	}
}
