package com.artisancode.fabrication;

import com.artisancode.fabrication.lambdas.Action1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionBuilder<T>
{
	protected Class<? extends T> target;
	protected FabricatorConfiguration configuration;
	protected int size;
	protected Behaviour state;
	protected int operationModifier;
	protected int secondaryModifier;
	protected int lastModificationStartIndex = 0;
	protected int lastModificationEndIndex = 0;
	protected List<List<Action1<T>>> modificationsArray;

	public CollectionBuilder(Class<? extends T> target)
	{
		this(target, new FabricatorConfiguration());
	}

	public CollectionBuilder(Class<? extends T> target, FabricatorConfiguration configuration)
	{
		this.target = target;
		this.configuration = configuration;
		state = Behaviour.ALL;

		initFoundations(5); // Default list size is 5
	}

	public CollectionBuilder<T> ofSize(int size)
	{
		initFoundations(size);
		return this;
	}

	protected void initFoundations(int size)
	{
		this.size = size;
		modificationsArray = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			modificationsArray.add(i, new LinkedList<>());
		}
	}

	public CollectionBuilder<T> all()
	{
		state = Behaviour.ALL;
		return this;
	}

	public CollectionBuilder<T> and(Action1<T> modifier)
	{
		return add(modifier);
	}

	public CollectionBuilder<T> with(Action1<T> modifier)
	{
		return add(modifier);
	}

	public CollectionBuilder<T> theFirst(int number)
	{
		state = Behaviour.FIRST;
		operationModifier = number;
		return this;
	}

	public CollectionBuilder<T> theNext(int number)
	{
		state = Behaviour.NEXT;
		operationModifier = number;
		return this;
	}

	public CollectionBuilder<T> theLast(int number)
	{
		state = Behaviour.LAST;
		operationModifier = number;
		return this;
	}

	public CollectionBuilder<T> thePrevious(int number)
	{
		state = Behaviour.PREVIOUS;
		operationModifier = number;
		return this;
	}

	public CollectionBuilder<T> everyNth(int number)
	{
		state = Behaviour.NTH;
		operationModifier = number;
		return this;
	}

	public CollectionBuilder<T> theSlice(int start, int end)
	{
		state = Behaviour.SLICE;
		operationModifier = start;
		secondaryModifier = end;
		return this;
	}

	public CollectionBuilder<T> random(int number)
	{
		state = Behaviour.RANDOM;
		operationModifier = number;
		return this;
	}

	public T fabricate()
	{
		return null;
	}

	protected CollectionBuilder<T> add(Action1<T> modifier)
	{
		switch (state)
		{
			case ALL:
			default:
			{
				handleGlobalModifications(modifier);
				break;
			}
			case FIRST:
			{
				handleFirstModifications(modifier);
				break;
			}
			case NEXT:
			{
				break;
			}
			case LAST:
			{
				handleLastModifications(modifier);
				break;
			}
			case PREVIOUS:
			{
				break;
			}
			case NTH:
			{
				break;
			}
			case SLICE:
			{
				handleSliceModifications(modifier);
				break;
			}
			case RANDOM:
			{
				break;
			}
		}

		return this;
	}

	private void handleSliceModifications(Action1<T> modifier)
	{
		if (operationModifier < 1)
		{
			throw new FabricationException(String.format("Unable to modify the start the slice at %d as the index to affect needs to be a positive integer", operationModifier));
		}

		if (operationModifier > size)
		{
			throw new FabricationException(String.format("Unable to slice as the start index %d is greater than the size of the collection %d", operationModifier, size));
		}

		if (secondaryModifier < 1)
		{
			throw new FabricationException(String.format("Unable to modify the end the slice at %d as the index to affect needs to be a positive integer", secondaryModifier));
		}

		if (secondaryModifier > size)
		{
			throw new FabricationException(String.format("Unable to slice as the end index %d is greater than the size of the collection %d", secondaryModifier, size));
		}

		if (operationModifier == secondaryModifier)
		{
			throw new FabricationException(String.format("Unable to slice as the start and end index are the same (%d)", secondaryModifier));
		}

		modifySlice(modifier, operationModifier, secondaryModifier);
	}

	private void modifySlice(Action1<T> modifier, int start, int end)
	{
		lastModificationStartIndex = start;
		lastModificationEndIndex = end;

		for (int i = lastModificationStartIndex; i <= lastModificationEndIndex; i++)
		{
			modificationsArray.get(i).add(modifier);
		}
	}

	private void handleLastModifications(Action1<T> modifier)
	{
		if (operationModifier < 1)
		{
			throw new FabricationException(String.format("Unable to modify the last %d elements as the number of elements to affect needs to be a positive integer", operationModifier));
		}

		if (operationModifier > size)
		{
			throw new FabricationException(String.format("Unable to modify the last %d elements as the list is only of size %d", operationModifier, size));
		}

		modifySlice(modifier, size - operationModifier, size - 1);
	}

	public void handleGlobalModifications(Action1<T> modifier)
	{
		for (List<Action1<T>> modifiers : modificationsArray)
		{
			modifiers.add(modifier);
		}
	}

	public void handleFirstModifications(Action1<T> modifier)
	{
		if (operationModifier < 1)
		{
			throw new FabricationException(String.format("Unable to modify the first %d elements as the number of elements to affect needs to be a positive integer", operationModifier));
		}

		if (operationModifier > size)
		{
			throw new FabricationException(String.format("Unable to modify the first %d elements as the list is only of size %d", operationModifier, size));
		}


		modifySlice(modifier, 0, operationModifier - 1);
	}

	public enum Behaviour
	{
		ALL,
		FIRST,
		NEXT,
		LAST,
		PREVIOUS,
		NTH,
		SLICE,
		RANDOM
	}
}
