package com.artisancode.fabrication;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JObjectBuilder<T>
{
	private Class<? extends T> target;
	private List<Action<T>> modifiers;

	public JObjectBuilder(Class<? extends T> target)
	{
		this.target = target;
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

			if(type == String.class)
			{
				field.set(result, field.getName());
			}
			else if(type == int.class)
			{
				field.set(result, 1);
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
