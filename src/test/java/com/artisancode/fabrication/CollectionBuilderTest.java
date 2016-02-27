package com.artisancode.fabrication;

import com.artisancode.fabrication.lambdas.Action1;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CollectionBuilderTest
{
	@Test
	public void handleFirstModifications_ValidNumberOfModifications_ModificationsAddedToFoundations()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		Action1<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
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
	public void handleLastModifications_ValidNumberOfModifications_ModificationsAddedToFoundations()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		Action1<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
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
	public void handleSliceModifications_ValidNumberOfModifications_ModificationsAddedToFoundations()
	{
		CollectionBuilder<FabricatorTests.TestObject> target = new CollectionBuilder<>(FabricatorTests.TestObject.class);

		Action1<FabricatorTests.TestObject> modifier = x -> x.name = "bob";
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
}