package com.artisancode.fabrication;

import com.artisancode.fabrication.lambdas.Action1;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectBuilder<T>
{
	private Class<? extends T> target;
	private FabricatorConfiguration configuration;
	private List<Action1<T>> modifiers;

	public ObjectBuilder(Class<? extends T> target, FabricatorConfiguration configuration)
	{
		this.target = target;
		this.configuration = configuration;
		modifiers = new ArrayList<>();
	}

	public ObjectBuilder<T> and(Action1<T> property)
	{
		return add(property);
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
		for (Action1<T> modifier : modifiers)
		{
			modifier.action(result);
		}

		return result;
	}

	public ObjectBuilder<T> with(Action1<T> property)
	{
		return add(property);
	}

	protected ObjectBuilder<T> add(Action1<T> property)
	{
		modifiers.add(property);
		return this;
	}
}
