package com.artisancode.fabrication;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
	protected List<List<Consumer<T>>> modificationsArray;
	protected Predicate<Integer> operationPredicate;
	protected Random random;
	protected Supplier<Integer> getRandomIndex = () -> random.nextInt(size);

	public CollectionBuilder(Class<? extends T> target)
	{
		this(target, new FabricatorConfiguration());
	}

	public CollectionBuilder(Class<? extends T> target, FabricatorConfiguration configuration)
	{
		this.target = target;
		this.configuration = configuration;
		state = Behaviour.ALL;

		initModificationsAndState(5); // Default list size is 5
	}

	public CollectionBuilder<T> ofSize(int size)
	{
		initModificationsAndState(size);
		return this;
	}

	protected void initModificationsAndState(int size)
	{
		this.size = size;
		modificationsArray = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			modificationsArray.add(i, new LinkedList<>());
		}

		// Set the initial state of the CollectionBuilder so that you can't use previous/next without other modifications
		lastModificationStartIndex = 0;
		lastModificationEndIndex = size - 1;
	}

	public CollectionBuilder<T> all()
	{
		state = Behaviour.ALL;
		return this;
	}

	public CollectionBuilder<T> and(Consumer<T> modifier)
	{
		return add(modifier);
	}

	public CollectionBuilder<T> with(Consumer<T> modifier)
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

	public CollectionBuilder<T> theNth(int number)
	{
		state = Behaviour.NTH;
		operationModifier = number;
		return this;
	}

	/**
	 * @param predicate A function that takes in the index and the o
	 * @return
	 */
	public CollectionBuilder<T> predicated(Predicate<Integer> predicate)
	{
		state = Behaviour.PREDICATE;
		operationPredicate = predicate;
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
		random = new Random((int) LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toEpochSecond());
		return this;
	}

	public T fabricate()
	{
		return null;
	}

	protected CollectionBuilder<T> add(Consumer<T> modifier)
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
				handleModifyTheNthElement(modifier);
				break;
			}
			case PREDICATE:
			{
				handlePredicatedModifications(modifier);
				break;
			}
			case SLICE:
			{
				handleSliceModifications(modifier);
				break;
			}
			case RANDOM:
			{
				handleRandomModifications(modifier);
				break;
			}
		}

		return this;
	}

	private void handleRandomModifications(Consumer<T> modifier)
	{
		if (operationModifier <= 0)
		{
			throw new FabricationException(String.format("Unable to modify %d number of random elements as the number of nodes to affect needs to be a positive integer", operationModifier));
		}

		if (operationModifier > size)
		{
			throw new FabricationException(String.format("Unable to modify %d number of random elements as the number of nodes to affect needs to less than or equal to the total size of the collection %d", operationModifier, size));
		}

		Set<Integer> indiciesToModify = new HashSet<>(size);

		while (indiciesToModify.size() != operationModifier)
		{
			indiciesToModify.add(getRandomIndex.get());
		}

		for (Integer index : indiciesToModify)
		{
			modificationsArray.get(index).add(modifier);
		}
	}

	protected void handlePredicatedModifications(Consumer<T> modifier)
	{
		boolean modified = false;
		for (int i = 0; i < modificationsArray.size(); i++)
		{
			// If the index matches the predicate, then add the modifier
			if (operationPredicate.test(i))
			{
				if (!modified)
				{
					// Only set the start index state the first time we set a modifier
					modified = true;
					lastModificationStartIndex = i;
				}

				modificationsArray.get(i).add(modifier);
				lastModificationEndIndex = i;
			}
		}
	}

	protected void handleModifyTheNthElement(Consumer<T> modifier)
	{
		if (operationModifier < 0)
		{
			throw new FabricationException(String.format("Unable to modify the nth element %d as the index to affect needs to be a positive integer or zero", operationModifier));
		}

		if (operationModifier >= size)
		{
			throw new FabricationException(String.format("Unable to modify the nth element %d as the index to affect needs to less than the total size of the collection %d", operationModifier, size));
		}

		modifySlice(modifier, operationModifier, operationModifier);
	}

	protected void handleSliceModifications(Consumer<T> modifier)
	{
		if (operationModifier < 0)
		{
			throw new FabricationException(String.format("Unable to modify the start of the slice at %d as the start index needs to be a positive integer or zero", secondaryModifier));
		}

		if (operationModifier > size - 2)
		{
			throw new FabricationException(String.format("Unable to modify the start of the slice at %d as the start index needs to be two less than the size of the collection", secondaryModifier));
		}

		if (secondaryModifier < 0)
		{
			throw new FabricationException(String.format("Unable to modify the end of the slice at %d as the end index needs to be a positive integer", secondaryModifier));
		}

		if (secondaryModifier >= size)
		{
			throw new FabricationException(String.format("Unable to slice as the end index %d is greater than the size of the collection %d", secondaryModifier, size));
		}

		if (operationModifier == secondaryModifier)
		{
			throw new FabricationException(String.format("Unable to slice as the start and end index are the same (%d)... Did you mean to use operation: theNth?", secondaryModifier));
		}

		modifySlice(modifier, operationModifier, secondaryModifier);
	}

	protected void handleLastModifications(Consumer<T> modifier)
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

	public void handleGlobalModifications(Consumer<T> modifier)
	{
		for (List<Consumer<T>> modifiers : modificationsArray)
		{
			modifiers.add(modifier);
		}
	}

	public void handleFirstModifications(Consumer<T> modifier)
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

	protected void modifySlice(Consumer<T> modifier, int start, int end)
	{
		lastModificationStartIndex = start;
		lastModificationEndIndex = end;

		for (int i = lastModificationStartIndex; i <= lastModificationEndIndex; i++)
		{
			modificationsArray.get(i).add(modifier);
		}
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
		PREDICATE,
		RANDOM
	}
}
