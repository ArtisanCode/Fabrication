package com.artisancode.fabrication;


import com.artisancode.fabrication.lambdas.Action1;

import java.util.ArrayList;
import java.util.List;

public class CollectionBuilder<T>
{
	private Class<? extends T> target;
	private FabricatorConfiguration configuration;
	private List<Action1<T>> modifiers;
	private int size;

	private List<ObjectBuilder<T>> foundations;

	public CollectionBuilder(Class<? extends T> target, FabricatorConfiguration configuration)
	{
		this.target = target;
		this.configuration = configuration;
		modifiers = new ArrayList<>();

		setDefaultValues();
	}

	public CollectionBuilder<T> ofSize(int size)
	{
		this.size = size;
		foundations = new ArrayList<>(size);
		return this;
	}

	protected void setDefaultValues()
	{
		this.size = 5;
	}

	public CollectionBuilder<T> all()
	{
		return this;
	}

	public CollectionBuilder<T> and(Action1<T> property)
	{
		return add(property);
	}


	public CollectionBuilder<T> with(Action1<T> property)
	{
		return add(property);
	}

	public CollectionBuilder<T> theFirst(int number)
	{

		return this;
	}

	public CollectionBuilder<T> theNext(int number)
	{
		return this;
	}

	public CollectionBuilder<T> theLast(int i)
	{
		return this;
	}

	public CollectionBuilder<T> thePrevious(int i)
	{
		return this;
	}

	public CollectionBuilder<T> everyNth(int i)
	{
		return this;
	}

	public CollectionBuilder<T> theSlice(int start, int end)
	{
		return this;
	}

	public CollectionBuilder<T> random(int i)
	{
		return this;
	}

	public T fabricate()
	{
		return null;
	}

	protected CollectionBuilder<T> add(Action1<T> property)
	{
		modifiers.add(property);
		return this;
	}
}
