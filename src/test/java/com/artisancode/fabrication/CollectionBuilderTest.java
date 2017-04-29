package com.artisancode.fabrication;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class CollectionBuilderTest
{

	@Test
	public void ofSize_ReinitializeState_SetStateAccordinglyAndCreateNewModificationsList()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);
		java.util.List<java.util.List<Consumer<FabricatorTests.TestObject>>> modifiersBefore = target.modificationsArray;

		int targetSize = 10;

		target.ofSize(targetSize);

		assertEquals(targetSize, target.size);
		assertNotNull(target.modificationsArray);
		assertNotSame(modifiersBefore, target.modificationsArray);
		assertEquals(targetSize, target.modificationsArray.size());
	}

	@Test
	public void handleAllModifications_AddModifier_ModificationsAddedToAll()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);
		int startIndex = 3;
		int endIndex = 4;

		target.lastModificationStartIndex = startIndex;
		target.lastModificationEndIndex = endIndex;

		Consumer<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
		target.all().with(modifier);

		// Check that the first 2 elements have modifications
		assertEquals(5, target.modificationsArray.size());
		assertEquals(1, target.modificationsArray.get(0).size());
		assertEquals(1, target.modificationsArray.get(1).size());
		assertEquals(1, target.modificationsArray.get(2).size());
		assertEquals(1, target.modificationsArray.get(3).size());
		assertEquals(1, target.modificationsArray.get(4).size());

		assertEquals(modifier, target.modificationsArray.get(0).get(0));
		assertEquals(modifier, target.modificationsArray.get(1).get(0));
		assertEquals(modifier, target.modificationsArray.get(2).get(0));
		assertEquals(modifier, target.modificationsArray.get(3).get(0));
		assertEquals(modifier, target.modificationsArray.get(4).get(0));

		// Check that the index state hasn't been changed
		assertEquals(startIndex, target.lastModificationStartIndex);
		assertEquals(endIndex, target.lastModificationEndIndex);
	}

	@Test
	public void handleRandomModifications_TwoModifier_ModificationsAddedToTwo()
	{
		testRandomMethods(1, Optional.empty());
		testRandomMethods(2, Optional.empty());
		testRandomMethods(3, Optional.empty());
		testRandomMethods(4, Optional.empty());
		testRandomMethods(5, Optional.empty());
		testRandomMethods(1, Optional.of(1234));
		testRandomMethods(2, Optional.of(1234));
		testRandomMethods(3, Optional.of(1234));
		testRandomMethods(4, Optional.of(1234));
		testRandomMethods(5, Optional.of(1234));
	}

	private void testRandomMethods(int numberOfModifications, Optional<Integer> seed)
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);
		int startIndex = 3;
		int endIndex = 4;

		target.lastModificationStartIndex = startIndex;
		target.lastModificationEndIndex = endIndex;

		Consumer<FabricatorTests.TestObject> modifier = x -> x.name = "bob";

		if (seed.isPresent()) {
			target.random(numberOfModifications, seed.get()).and(modifier);
		} else {
			target.random(numberOfModifications).and(modifier);
		}

		// Check that the first 2 elements have modifications
		assertEquals(5, target.modificationsArray.size());
		Object[] modArray = target.modificationsArray.stream().filter(x -> x.size() == 1).toArray();

		assertEquals(numberOfModifications, modArray.length);

		// Check that the index state hasn't been changed
		assertEquals(startIndex, target.lastModificationStartIndex);
		assertEquals(endIndex, target.lastModificationEndIndex);
	}

	@Test(expected = FabricationException.class)
	public void handleRandomModifications_ZeroNumberOfModifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.random(0).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleRandomModifications_100Modifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.random(100).with(x -> x.name = "bob");
	}

	@Test
	public void handlePredicateModifications_AddModifier_ModificationsAddedToEvenIndiciesOnly()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);
		int startIndex = 2;
		int endIndex = 4;

		target.lastModificationStartIndex = 0;
		target.lastModificationEndIndex = 0;

		Consumer<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
		target.predicated(i -> i % 2 == 0 && i != 0).with(modifier);

		// Check that the first 2 elements have modifications
		assertEquals(5, target.modificationsArray.size());
		assertEquals(0, target.modificationsArray.get(0).size());
		assertEquals(0, target.modificationsArray.get(1).size());
		assertEquals(1, target.modificationsArray.get(2).size());
		assertEquals(0, target.modificationsArray.get(3).size());
		assertEquals(1, target.modificationsArray.get(4).size());

		assertEquals(modifier, target.modificationsArray.get(2).get(0));
		assertEquals(modifier, target.modificationsArray.get(4).get(0));

		// Check that the index state has been set correctly
		assertEquals(startIndex, target.lastModificationStartIndex);
		assertEquals(endIndex, target.lastModificationEndIndex);
	}

	@Test
	public void handleFirstModifications_ValidNumberOfModifications_ModificationsAdded()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		Consumer<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
		target.theFirst(2).with(modifier);

		// Check that the first 2 elements have modifications
		assertEquals(5, target.modificationsArray.size());
		assertEquals(1, target.modificationsArray.get(0).size());
		assertEquals(1, target.modificationsArray.get(1).size());
		assertEquals(0, target.modificationsArray.get(2).size());
		assertEquals(0, target.modificationsArray.get(3).size());
		assertEquals(0, target.modificationsArray.get(4).size());

		assertEquals(modifier, target.modificationsArray.get(0).get(0));
		assertEquals(modifier, target.modificationsArray.get(1).get(0));

		// Check that the state has been modified correctly
		assertEquals(0, target.lastModificationStartIndex);
		assertEquals(1, target.lastModificationEndIndex);
	}

	@Test(expected = FabricationException.class)
	public void handleFirstModifications_ZeroNumberOfModifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theFirst(0).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleFirstModifications_100Modifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theFirst(100).with(x -> x.name = "bob");
	}

	@Test
	public void handleLastModifications_ValidNumberOfModifications_ModificationsAdded()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		Consumer<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
		target.theLast(2).with(modifier);

		// Check that the first 2 elements have modifications
		assertEquals(5, target.modificationsArray.size());
		assertEquals(0, target.modificationsArray.get(0).size());
		assertEquals(0, target.modificationsArray.get(1).size());
		assertEquals(0, target.modificationsArray.get(2).size());
		assertEquals(1, target.modificationsArray.get(3).size());
		assertEquals(1, target.modificationsArray.get(4).size());

		assertEquals(modifier, target.modificationsArray.get(3).get(0));
		assertEquals(modifier, target.modificationsArray.get(4).get(0));

		// Check that the state has been modified correctly
		assertEquals(3, target.lastModificationStartIndex);
		assertEquals(4, target.lastModificationEndIndex);
	}

	@Test(expected = FabricationException.class)
	public void handleLastModifications_ZeroNumberOfModifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theLast(0).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleLastModifications_100Modifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theLast(100).with(x -> x.name = "bob");
	}

	@Test
	public void handleSliceModifications_ValidNumberOfModifications_ModificationsAdded()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		Consumer<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
		target.theSlice(1, 3).with(modifier);

		// Check that the first 2 elements have modifications
		assertEquals(5, target.modificationsArray.size());
		assertEquals(0, target.modificationsArray.get(0).size());
		assertEquals(1, target.modificationsArray.get(1).size());
		assertEquals(1, target.modificationsArray.get(2).size());
		assertEquals(1, target.modificationsArray.get(3).size());
		assertEquals(0, target.modificationsArray.get(4).size());

		assertEquals(modifier, target.modificationsArray.get(1).get(0));
		assertEquals(modifier, target.modificationsArray.get(2).get(0));
		assertEquals(modifier, target.modificationsArray.get(3).get(0));

		// Check that the state has been modified correctly
		assertEquals(1, target.lastModificationStartIndex);
		assertEquals(3, target.lastModificationEndIndex);
	}

	@Test(expected = FabricationException.class)
	public void handleSliceModifications_ZeroNumberOfModifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theSlice(0, 0).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleSliceModifications_SliceEndOutOfRange_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theSlice(0, 100).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleSliceModifications_SliceStartOutOfRange_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theSlice(100, 3).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleSliceModifications_SliceEndNegative_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theSlice(0, -5).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleSliceModifications_SliceStartNegative_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theSlice(-5, 3).with(x -> x.name = "bob");
	}


	@Test
	public void handleTheNthModifications_ModifyThe4thElement_ModificationsAdded()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		Consumer<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
		target.theNth(3).with(modifier);

		// Check that the first 2 elements have modifications
		assertEquals(5, target.modificationsArray.size());
		assertEquals(0, target.modificationsArray.get(0).size());
		assertEquals(0, target.modificationsArray.get(1).size());
		assertEquals(0, target.modificationsArray.get(2).size());
		assertEquals(1, target.modificationsArray.get(3).size());
		assertEquals(0, target.modificationsArray.get(4).size());

		assertEquals(modifier, target.modificationsArray.get(3).get(0));

		// Check that the state has been modified correctly
		assertEquals(3, target.lastModificationStartIndex);
		assertEquals(3, target.lastModificationEndIndex);
	}

	@Test(expected = FabricationException.class)
	public void handleTheNthModifications_NegativeNumberOfModifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		target.theNth(-1).with(x -> x.name = "bob");
	}

	@Test(expected = FabricationException.class)
	public void handleTheNthModifications_5ThModifications_FabricationExceptionThrown()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		// Expect to throw as array is 0-based
		target.theNth(5).with(x -> x.name = "bob");
	}
}