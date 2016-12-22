package com.artisancode.fabrication;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

public class FabricatorConfiguration
{
	public HashMap<Class<?>, Supplier<Object>> customGenerators = new HashMap<>();
	public boolean useFieldNameForString;
	public boolean recursive;
	public int recurseLimit;
	public int generationSeed;
	// Temporal helpers
	public Supplier<Date> currentDate = () -> Date.from(Instant.now());
	public Supplier<Instant> currentInstant = () -> Instant.now();
	public Supplier<ZonedDateTime> currentZonedDateTime = () -> ZonedDateTime.now(ZoneOffset.UTC);
	public Supplier<LocalDateTime> currentLocalDateTime = () -> ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
	protected HashMap<Class<?>, Supplier<Object>> defaultGenerators = new HashMap<>();

	public FabricatorConfiguration()
	{
		this(0);
	}

	public FabricatorConfiguration(int generationSeed)
	{
		this.generationSeed = generationSeed;
		recursive = true;
		recurseLimit = 5;
		useFieldNameForString = true;

		initDefaultGenerators();
	}

	FabricatorConfiguration(int generationSeed,
	                        boolean useFieldNameForString,
	                        boolean recursive,
	                        int recurseLimit,
	                        Supplier<Date> currentDate,
	                        Supplier<Instant> currentInstant,
	                        Supplier<ZonedDateTime> currentZonedDateTime,
	                        Supplier<LocalDateTime> currentLocalDateTime)
	{
		this(generationSeed);
		this.useFieldNameForString = useFieldNameForString;
		this.recursive = recursive;
		this.recurseLimit = recurseLimit;
		this.currentDate = currentDate;
		this.currentInstant = currentInstant;
		this.currentZonedDateTime = currentZonedDateTime;
		this.currentLocalDateTime = currentLocalDateTime;
	}

	void initDefaultGenerators()
	{
		defaultGenerators.put(int.class, () -> generationSeed);
		defaultGenerators.put(BigDecimal.class, () -> generationSeed);
		defaultGenerators.put(double.class, () -> (double) generationSeed);
		defaultGenerators.put(byte.class, () -> (byte) generationSeed);
		defaultGenerators.put(short.class, () -> (short) generationSeed);
		defaultGenerators.put(long.class, () -> (long) generationSeed);
		defaultGenerators.put(float.class, () -> (float) generationSeed);
		defaultGenerators.put(char.class, () -> (char) ('A' + generationSeed));
		defaultGenerators.put(boolean.class, () -> false);
		defaultGenerators.put(String.class, () -> Integer.toString(generationSeed));

		// Temporal generators
		defaultGenerators.put(Date.class, () -> currentDate.get());
		defaultGenerators.put(Instant.class, () -> currentInstant.get());
		defaultGenerators.put(ZonedDateTime.class, () -> currentZonedDateTime.get());
		defaultGenerators.put(LocalDateTime.class, () -> currentLocalDateTime.get());
	}

	public Object generate(Class<?> targetClass, String fieldName)
	{
		Supplier<Object> generator = Optional.ofNullable(customGenerators.get(targetClass))
				                             .orElse(defaultGenerators.get(targetClass));

		if (targetClass == String.class && useFieldNameForString && fieldName != null)
		{
			// Special case for Strings that use the fieldName
			return fieldName;
		}

		if (generator != null)
		{
			// A generator exists ... use it!
			return generator.get();
		}

		if (targetClass.isEnum())
		{
			// Default the value to the first value in the Enum
			Class<? extends Enum<?>> targetEnumClass = (Class<? extends Enum<?>>) targetClass;
			return targetEnumClass.getEnumConstants()[0];
		}

		// We can't fabricate interfaces or abstract classes
		boolean canFabricate = !targetClass.isInterface() && !Modifier.isAbstract(targetClass.getModifiers());
		boolean shouldFabricate = recursive && recurseLimit > 0;
		if (canFabricate && shouldFabricate)
		{
			// If recursing and there is at least one more level to go, try and generate the sub-object
			ObjectBuilder<Object> builder = new ObjectBuilder(targetClass, cloneForNextGeneration());
			return builder.fabricate();
		}

		// If all else fails
		return null;
	}

	FabricatorConfiguration cloneForNextGeneration()
	{
		FabricatorConfiguration result = new FabricatorConfiguration(generationSeed + 1,
				                                                            useFieldNameForString,
				                                                            recursive,
				                                                            recurseLimit - 1,
				                                                            currentDate,
				                                                            currentInstant,
				                                                            currentZonedDateTime,
				                                                            currentLocalDateTime);

		// Copy across any custom generators that exist
		result.customGenerators.putAll(customGenerators);

		return result;
	}
}
