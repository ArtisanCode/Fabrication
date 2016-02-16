package com.artisancode.fabrication;

import java.util.HashMap;

public class FabricatorConfiguration
{
	private HashMap<Class<?>, Func<Object>> generators;

	private int generation;

	private boolean recursive;

	private boolean useFieldNameForString;

	private int recurseLimit;

	public FabricatorConfiguration()
	{
		this.generation = 0;

		setDefaultValues();
		init();
	}

	public FabricatorConfiguration(int generation)
	{
		this.generation = generation;

		setDefaultValues();
		init();
	}

	public void init()
	{
		generators = new HashMap<>();

		generators.put(int.class, () -> generation);
		generators.put(double.class, () -> (double) generation);
		generators.put(byte.class, () -> (byte) generation);
		generators.put(short.class, () -> (short) generation);
		generators.put(long.class, () -> (long) generation);
		generators.put(float.class, () -> (float) generation);
		generators.put(char.class, () -> Character.toString((char) (65 + generation)));
		generators.put(boolean.class, () -> false);
		generators.put(String.class, () -> Integer.toString(generation));
	}

	public HashMap<Class<?>, Func<Object>> getGenerators()
	{
		return generators;
	}

	public void setGenerators(HashMap<Class<?>, Func<Object>> generators)
	{
		this.generators = generators;
	}

	public int getGeneration()
	{
		return generation;
	}

	public boolean isRecursive()
	{
		return recursive;
	}

	public void setRecursive(boolean recursive)
	{
		this.recursive = recursive;
	}

	public boolean isFieldNamesUsedForStrings()
	{
		return useFieldNameForString;
	}

	public void setUseFieldNameForString(boolean useFieldNameForString)
	{
		this.useFieldNameForString = useFieldNameForString;
	}

	private void setDefaultValues()
	{
		recursive = false;
		recurseLimit = 5;
		useFieldNameForString = true;
	}
}
