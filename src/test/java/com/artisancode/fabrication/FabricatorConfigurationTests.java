package com.artisancode.fabrication;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class FabricatorConfigurationTests
{
	@Test
	public void testGenerate() throws Exception
	{
		testGenerateRunner(int.class, 0);
		testGenerateRunner(double.class, 0d);
		testGenerateRunner(byte.class, (byte) 0);
		testGenerateRunner(short.class, (short) 0);
		testGenerateRunner(long.class, 0L);
		testGenerateRunner(float.class, 0f);
		testGenerateRunner(char.class, 'A');
		testGenerateRunner(boolean.class, false);
		testGenerateRunner(String.class, "0");
	}

	public <T> void testGenerateRunner(Class<T> targetClass, T expectedResult)
	{
		FabricatorConfiguration target = new FabricatorConfiguration();

		Object actualResult = target.generate(targetClass, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithEnum()
	{
		testGenerateRunner(TestEnum.class, TestEnum.FIRST);
	}

	@Test
	public void testDefaultGenerateWithDateTime()
	{
		Date expectedResult = Date.from(Instant.now());

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentDate = () -> expectedResult;

		Object actualResult = target.generate(Date.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithInstant()
	{
		Instant expectedResult = Instant.now();

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentInstant = () -> expectedResult;

		Object actualResult = target.generate(Instant.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithZonedDateTime()
	{
		ZonedDateTime expectedResult = ZonedDateTime.now(ZoneOffset.UTC);

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentZonedDateTime = () -> expectedResult;

		Object actualResult = target.generate(ZonedDateTime.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithLocalDateTime()
	{
		LocalDateTime expectedResult = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentLocalDateTime = () -> expectedResult;

		Object actualResult = target.generate(LocalDateTime.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testGenerateWithCustomEnumFunc()
	{

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.generators.put(TestEnum.class, () -> TestEnum.THIRD);

		Object actualResult = target.generate(TestEnum.class, null);

		assertEquals(TestEnum.THIRD, actualResult);
	}

	@Test
	public void testGenerateWithStringFieldName()
	{

		FabricatorConfiguration target = new FabricatorConfiguration();
		String fieldName = "myFieldName";

		Object actualResult = target.generate(String.class, fieldName);

		assertEquals(fieldName, actualResult);
	}

	@Test
	public void testGenerateWithInterface()
	{

		FabricatorConfiguration target = new FabricatorConfiguration();

		Object actualResult = target.generate(TestInterface.class, null);

		assertEquals(null, actualResult);
	}

	@Test
	public void testGenerateWithRecursiveFieldGeneration()
	{

		FabricatorConfiguration target = new FabricatorConfiguration();

		Object actualResult = target.generate(TestClassWithObjectField.class, null);

		assertEquals(null, actualResult);
	}

	public enum TestEnum
	{
		FIRST,
		SECOND,
		THIRD
	}

	public interface TestInterface
	{
	}

	public class TestClass
	{
		public int age;
		public String name;
	}

	public class TestClassWithObjectField
	{
		public boolean flag;
		public int generation;
		TestClass classField;
	}


}