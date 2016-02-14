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
				                    .with(x -> x.setName(testName))
				                    .add(x -> x.setAge(testAge))
				                    .fabricate();

		assertEquals(testName, result.getName());
		assertEquals(testAge, result.getAge());
	}

	@Test
	public void testFabricationChain_StringOnlySpecifiedFabrication_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		String testName = "TestName";

		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
				                    .with(x -> x.setName(testName))
				                    .fabricate();

		assertEquals(testName, result.getName());
		assertEquals(1, result.getAge());
	}

	@Test
	public void testFabricationChain_IntOnlySpecifiedFabrication_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		int testAge = 5;

		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
				                    .with(x -> x.setAge(testAge))
				                    .fabricate();

		assertEquals("name", result.getName());
		assertEquals(testAge, result.getAge());
	}

	@Test
	public void testFabricationChain_ZeroSpecifiedFabrication_ObjectGeneratedAndCreatedCorrectly() throws Exception
	{
		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
				                    .fabricate();

		assertEquals("name", result.getName());
		assertEquals(1, result.getAge());
	}

	public class TestObject
	{
		private String name;
		private int age;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public int getAge()
		{
			return age;
		}

		public void setAge(int age)
		{
			this.age = age;
		}
	}
}