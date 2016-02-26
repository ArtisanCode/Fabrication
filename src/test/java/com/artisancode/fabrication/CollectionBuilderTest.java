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
}