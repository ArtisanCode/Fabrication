package com.artisancode.fabrication;

import org.junit.Test;

import static org.junit.Assert.*;

public class FabricatorConfigurationTest
{
	@Test
	public void testGenerate() throws Exception
	{
		testGenerateRunner(int.class, 0);
		testGenerateRunner(double.class, 0d);
		testGenerateRunner(byte.class, (byte)0);
		testGenerateRunner(short.class, (short)0);
		testGenerateRunner(long.class, 0l);
		testGenerateRunner(float.class, 0f);
		testGenerateRunner(char.class, 'A');
		testGenerateRunner(boolean.class, false);
		testGenerateRunner(String.class, "0");
	}

	public <T> void testGenerateRunner(Class<T> targetClass, T expectedResult) {
		FabricatorConfiguration target = new FabricatorConfiguration();

		Object actualResult = target.generate(targetClass, null);

		assertEquals(expectedResult, actualResult);
	}

	public enum TestEnum{
		FIRST,
		SECOND,
		THIRD
	}

	@Test
	public void testDefaultGenerateWithEnum() {
		testGenerateRunner(TestEnum.class, TestEnum.FIRST);
	}

	@Test
	public void testGenerateWithCustomEnumFunc() {

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.generators.put(TestEnum.class, () -> TestEnum.THIRD);

		Object actualResult = target.generate(TestEnum.class, null);

		assertEquals(TestEnum.THIRD, actualResult);
	}

	@Test
	public void testGenerateWithStringFieldName() {

		FabricatorConfiguration target = new FabricatorConfiguration();
		String fieldName = "myFieldName";

		Object actualResult = target.generate(String.class, fieldName);

		assertEquals(fieldName, actualResult);
	}
}