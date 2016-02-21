package com.artisancode.fabrication;

import com.artisancode.fabrication.lambdas.Action;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
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
			field.set(result, configuration.generate(type, field.getName()));
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
