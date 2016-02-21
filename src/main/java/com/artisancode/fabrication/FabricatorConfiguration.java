package com.artisancode.fabrication;

import com.artisancode.fabrication.lambdas.Func;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class FabricatorConfiguration
{
	public HashMap<Class<?>, Func<Object>> customGenerators = new HashMap<>();
	public boolean useFieldNameForString;
	public boolean recursive;
	public int recurseLimit;
	public int generationSeed;
	// Temporal helpers
	public Func<Date> currentDate = () -> Date.from(Instant.now());
	public Func<Instant> currentInstant = () -> Instant.now();
	public Func<ZonedDateTime> currentZonedDateTime = () -> ZonedDateTime.now(ZoneOffset.UTC);
	public Func<LocalDateTime> currentLocalDateTime = () -> ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
	protected HashMap<Class<?>, Func<Object>> defaultGenerators = new HashMap<>();

	public FabricatorConfiguration()
	{
		this.generationSeed = 0;

		setDefaultValues();
		initDefaultGenerators();
	}

	public FabricatorConfiguration(int generationSeed)
	{
		this.generationSeed = generationSeed;

		setDefaultValues();
		initDefaultGenerators();
	}

	public void initDefaultGenerators()
	{
		defaultGenerators.put(int.class, () -> generationSeed);
		defaultGenerators.put(double.class, () -> (double) generationSeed);
		defaultGenerators.put(byte.class, () -> (byte) generationSeed);
		defaultGenerators.put(short.class, () -> (short) generationSeed);
		defaultGenerators.put(long.class, () -> (long) generationSeed);
		defaultGenerators.put(float.class, () -> (float) generationSeed);
		defaultGenerators.put(char.class, () -> (char) ('A' + generationSeed));
		defaultGenerators.put(boolean.class, () -> false);
		defaultGenerators.put(String.class, () -> Integer.toString(generationSeed));

		// Temporal generators
		defaultGenerators.put(Date.class, () -> currentDate.func());
		defaultGenerators.put(Instant.class, () -> currentInstant.func());
		defaultGenerators.put(ZonedDateTime.class, () -> currentZonedDateTime.func());
		defaultGenerators.put(LocalDateTime.class, () -> currentLocalDateTime.func());
	}

	public Object generate(Class<?> targetClass, String fieldName) throws IllegalAccessException
	{
		Func<Object> generator = Optional.ofNullable(customGenerators.get(targetClass))
				                         .orElse(defaultGenerators.get(targetClass));

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

		if (!targetClass.isInterface() && recursive && recurseLimit > 0)
		{
			JObjectBuilder<Object> builder = new JObjectBuilder(targetClass, cloneForNextGeneration());
			return builder.fabricate();
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
		FabricatorConfiguration result = new FabricatorConfiguration(generationSeed + 1);

		result.useFieldNameForString = useFieldNameForString;
		result.recursive = recursive;
		result.recurseLimit = recurseLimit - 1;
		result.currentDate = currentDate;
		result.currentInstant = currentInstant;
		result.currentZonedDateTime = currentZonedDateTime;
		result.currentLocalDateTime = currentLocalDateTime;

		// Copy across any custom generators that exist
		customGenerators.entrySet().stream().forEach(x -> result.customGenerators.put(x.getKey(), x.getValue()));

		return result;
	}
}
