package com.artisancode.fabrication;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FabricatorTests
{
	@Test
	public void testFabricationChain_FullySpecifiedFabrication_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		String testName = "TestName";
		int testAge = 5;

		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
				                    .with(x -> x.name = testName)
				                    .add(x -> x.age = testAge)
				                    .fabricate();

		assertEquals(testName, result.name);
		assertEquals(testAge, result.age);
	}

	@Test
	public void testFabricationChain_StringOnlySpecifiedFabrication_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		String testName = "TestName";

		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
				                    .with(x -> x.name = testName)
				                    .fabricate();

		assertEquals(testName, result.name);
		assertEquals(0, result.age);
	}

	@Test
	public void testFabricationChain_IntOnlySpecifiedFabrication_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		int testAge = 5;

		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
				                    .with(x -> x.age = testAge)
				                    .fabricate();

		assertEquals("name", result.name);
		assertEquals(testAge, result.age);
	}

	@Test
	public void testFabricationChain_ZeroSpecifiedFabrication_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
				                    .fabricate();

		assertEquals("name", result.name);
		assertEquals(0, result.age);
	}

	@Test
	public void testFabricationChain_InheritedFieldsReferenceInheritedClass_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		boolean expectedInheritedFlag = true;

		InheritedTestObject result = new Fabricator<InheritedTestObject>()
				                    .createNew(InheritedTestObject.class)
				                    .with(x -> x.inheritedFlag = expectedInheritedFlag)
				                    .fabricate();

		assertEquals("name", result.name);
		assertEquals(0, result.age);
		assertEquals(expectedInheritedFlag, result.inheritedFlag);
	}

	@Test
	public void testFabricationChain_CustomConfiguration_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		int expectedResult = 45678;
		FabricatorConfiguration testConfig = new FabricatorConfiguration();
		testConfig.customGenerators.put(int.class, () -> expectedResult);

		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class, testConfig)
				                    .fabricate();

		assertEquals("name", result.name);
		assertEquals(expectedResult, result.age);
	}


	@Test
	public void testFluentCollectionInterface()
	{

		TestObject result = new Fabricator<TestObject>()
				                    .createNewCollection(TestObject.class).ofSize(100)
				                    .all()
				                    .with(x -> x.name = "bob")
				                    .theFirst(2)
				                    .with(x -> x.title = "Mr")
				                    .and(x -> x.age = 20)
				                    .theNext(2)
				                    .with(x -> x.title = "Mrs")
				                    .and(x -> x.age = 23)
				                    .theLast(1)
				                    .with(x -> x.title = "Miss")
				                    .and(x -> x.age = 5)
				                    .thePrevious(2)
				                    .with(x -> x.age = 4)
				                    .everyNth(3)
				                    .with(x -> x.title = "Dr")
				                    .and(x -> x.age = 50)
				                    .theSlice(0, 3)
				                    .with(x -> x.name = "ted")
				                    .random(3)
				                    .with(x -> x.hungry = true)
				                    .fabricate();

//		FabricatorConfiguration testConfig = new FabricatorConfiguration();
//
//      // This is the interface I would like!
//		TestObject result = new Fabricator<TestObject>()
//				                    .createNewCollection(TestObject.class).ofSize(10)
//									.all()
//										.with(x -> x.name ="bob")
//										.and(x->x.age = 1);
//									.theFirst(2)
//										.with(x->x.title = "Mr")
//										.and(x->x.age = 20)
//									.theNext(2)
//					                    .with(x->x.title = "Mrs")
//					                    .and(x->x.age = 23)
//									.theLast(1)
//					                    .with(x->x.title = "Miss")
//					                    .and(x->x.age = 5)
//									.thePrevious(2)
//										.with(x->x.age=4)
//									.theNth(5)
//					                    .with(x->x.title = "Dr")
//				                        .and(x->x.age = 50)
//									.theSlice(0,3)
//				                        .with(x->x.name = "ted")
//									.random(3)
//										.with(x->x.hungry = true);
	}

	public class TestObject
	{
		public String name;
		public String title;
		public int age;
		public boolean hungry;
	}

	public class InheritedTestObject extends TestObject
	{
		public boolean inheritedFlag;
	}
}