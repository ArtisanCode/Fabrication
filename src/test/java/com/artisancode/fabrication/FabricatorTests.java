package com.artisancode.fabrication;

import org.junit.*;

import static org.junit.Assert.*;

public class FabricatorTests
{

	public class TestObject {
		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		private String name;

		public int getAge()
		{
			return age;
		}

		public void setAge(int age)
		{
			this.age = age;
		}

		private int age;
	}

	@Test
	public void testFabrigationChain_FullySpeccedFabrication_ObjectGeneratedAndInstansiatedCorrectly() throws Exception
	{
		String testName = "TestName";
		int testAge = 5;

		TestObject result = new Fabricator<TestObject>()
				                    .createNew(TestObject.class)
									.with(x -> x.setName(testName))
									.add(x->x.setAge(testAge))
				                    .fabricate();

		assertEquals(testName, result.getName());
		assertEquals(testAge, result.getAge());
	}
}