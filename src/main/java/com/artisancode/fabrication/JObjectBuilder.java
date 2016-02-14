package com.artisancode.fabrication;

import org.objenesis.ObjenesisStd;

import java.util.ArrayList;
import java.util.List;

public class JObjectBuilder<T>
{
	private Class<? extends T> target;
	private List<Action<T>> modifiers;

	public JObjectBuilder(Class<? extends T> target){
		this.target = target;
		modifiers = new ArrayList<>();
	}

	public JObjectBuilder<T> and(Action<T> property) {
		return add(property);
	}

	public T fabricate(){
		ObjenesisStd ctor = new ObjenesisStd();
		T result = ctor.getInstantiatorOf(target).newInstance();

		for (Action<T> modifier : modifiers)
		{
			modifier.action(result);
		}

		return result;
	}

	public JObjectBuilder<T> with(Action<T> property) {
		return add(property);
	}

	protected JObjectBuilder<T> add(Action<T> property) {
		modifiers.add(property);
		return this;
	}
}
