package com.artisancode.fabrication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;

public class FabricatorConfiguration
{
	public HashMap<Class<?>, Func<Object>> generators;
	public boolean useFieldNameForString;
	public boolean recursive;
	public int recurseLimit;
	public int generationSeed;

	// Temporal helpers
	public Func<Date> currentDate = () -> Date.from(Instant.now());
	public Func<Instant> currentInstant = () -> Instant.now();
	public Func<ZonedDateTime> currentZonedDateTime = () -> ZonedDateTime.now(ZoneOffset.UTC);
	public Func<LocalDateTime> currentLocalDateTime = () -> ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();

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

		generators.put(int.class, () -> getGenerationSeed());
		generators.put(double.class, () -> (double) getGenerationSeed());
		generators.put(byte.class, () -> (byte) getGenerationSeed());
		generators.put(short.class, () -> (short) getGenerationSeed());
		generators.put(long.class, () -> (long) getGenerationSeed());
		generators.put(float.class, () -> (float) getGenerationSeed());
		generators.put(char.class, () -> (char) ('A' + getGenerationSeed()));
		generators.put(boolean.class, () -> false);
		generators.put(String.class, () -> Integer.toString(getGenerationSeed()));

		// Temporal generators
		generators.put(Date.class, () -> currentDate.func());
		generators.put(Instant.class, () -> currentInstant.func());
		generators.put(ZonedDateTime.class, () -> currentZonedDateTime.func());
		generators.put(LocalDateTime.class, () -> currentLocalDateTime.func());
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

		if (!targetClass.isInterface() && recursive)
		{
			JObjectBuilder<Object> builder = new JObjectBuilder(targetClass, cloneForNextGeneration());
			try
			{
				return builder.fabricate();
			}
			catch (Exception ex)
			{
				// Don't care if generation fails...
				return null;
			}
		}

		return null;
	}

	public void setDefaultValues()
	{
		recursive = true;
		recurseLimit = 5;
		useFieldNameForString = true;
	}

	public FabricatorConfiguration cloneForNextGeneration()
	{
		FabricatorConfiguration result = new FabricatorConfiguration();

		result.useFieldNameForString = useFieldNameForString;
		result.recursive = recursive;
		result.recurseLimit = recurseLimit;
		result.generators = generators;
		result.currentDate = currentDate;
		result.currentInstant = currentInstant;
		result.currentZonedDateTime = currentZonedDateTime;
		result.currentLocalDateTime = currentLocalDateTime;

		result.generationSeed = generationSeed + 1;

		return result;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof FabricatorConfiguration)) return false;

		FabricatorConfiguration that = (FabricatorConfiguration) o;

		if (useFieldNameForString != that.useFieldNameForString) return false;
		if (recursive != that.recursive) return false;
		if (recurseLimit != that.recurseLimit) return false;
		if (generationSeed != that.generationSeed) return false;
		if (!generators.equals(that.generators)) return false;
		if (!currentDate.equals(that.currentDate)) return false;
		if (!currentInstant.equals(that.currentInstant)) return false;
		if (!currentZonedDateTime.equals(that.currentZonedDateTime)) return false;
		return currentLocalDateTime.equals(that.currentLocalDateTime);

	}

	@Override
	public int hashCode()
	{
		int result = generators.hashCode();
		result = 31 * result + (useFieldNameForString ? 1 : 0);
		result = 31 * result + (recursive ? 1 : 0);
		result = 31 * result + recurseLimit;
		result = 31 * result + generationSeed;
		result = 31 * result + currentDate.hashCode();
		result = 31 * result + currentInstant.hashCode();
		result = 31 * result + currentZonedDateTime.hashCode();
		result = 31 * result + currentLocalDateTime.hashCode();
		return result;
	}

	protected int getGenerationSeed()
	{
		return generationSeed;
	}
}
