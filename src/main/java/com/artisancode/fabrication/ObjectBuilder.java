package com.artisancode.fabrication;

import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ObjectBuilder<T>
{
	protected Class<? extends T> target;
	protected FabricatorConfiguration configuration;
	protected List<Consumer<T>> modifiers;

	public ObjectBuilder(Class<? extends T> target, FabricatorConfiguration configuration)
	{
		this.target = target;
		this.configuration = configuration;
		modifiers = new ArrayList<>();
	}

	public T fabricate()
	{
		ObjenesisStd ctor = new ObjenesisStd();
		T result = ctor.getInstantiatorOf(target).newInstance();

		List<Field> fieldsToTarget = new ArrayList<>();

		Class<?> classWithFields = result.getClass();
		while (classWithFields.getSuperclass() != null) // we don't want to process Object.class
		{
			Collections.addAll(fieldsToTarget, classWithFields.getDeclaredFields());
			classWithFields = classWithFields.getSuperclass();
		}
		fieldsToTarget.removeIf(x -> x.getName() == "this$0"); // remove the .this fields

		// Fill the object with default values
		for (Field field : fieldsToTarget)
		{
			Class<?> type = field.getType();
			field.setAccessible(true);
			try
			{
				field.set(result, configuration.generate(type, field.getName()));
			}
			catch (IllegalAccessException e)
			{
				throw new FabricationException(e);
			}
		}

		// Perform the specific object test modifications
		for (Consumer<T> modifier : modifiers)
		{
			modifier.accept(result);
		}

		return result;
	}

	public ObjectBuilder<T> with(Consumer<T> property)
	{
		return add(property);
	}


	public ObjectBuilder<T> and(Consumer<T> property)
	{
		return add(property);
	}

	protected ObjectBuilder<T> add(Consumer<T> property)
	{
		modifiers.add(property);
		return this;
	}
}
