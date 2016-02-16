package com.artisancode.fabrication;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JObjectBuilder<T>
{
	private Class<? extends T> target;
	private FabricatorConfiguration configuration;
	private List<Action<T>> modifiers;

	public JObjectBuilder(Class<? extends T> target, FabricatorConfiguration configuration)
	{
		this.target = target;
		this.configuration = configuration;
		modifiers = new ArrayList<>();
	}

	public JObjectBuilder<T> and(Action<T> property)
	{
		return add(property);
	}

	public T fabricate() throws IllegalAccessException
	{
		ObjenesisStd ctor = new ObjenesisStd();
		T result = ctor.getInstantiatorOf(target).newInstance();

		// Fill the object with default values
		for (Field field : result.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Class<?> type = field.getType();

			if (type == String.class && configuration.isFieldNamesUsedForStrings())
			{
				// Special case for Strings that use the fieldName
				field.set(result, field.getName());
				continue;
			}

			if (configuration.getGenerators().containsKey(type))
			{
				Func<Object> generator = configuration.getGenerators().get(type);
				field.set(result, generator.func());
				continue;
			}
		}

		// Perform the specific object test modifications
		for (Action<T> modifier : modifiers)
		{
			modifier.action(result);
		}

		return result;
	}

	public JObjectBuilder<T> with(Action<T> property)
	{
		return add(property);
	}

	protected JObjectBuilder<T> add(Action<T> property)
	{
		modifiers.add(property);
		return this;
	}
}
