package com.artisancode.fabrication;

import com.artisancode.fabrication.lambdas.Action2;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.*;

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

	public <T> void testGenerateRunner(Class<T> targetClass, T expectedResult) throws IllegalAccessException
	{
		FabricatorConfiguration target = new FabricatorConfiguration();

		Object actualResult = target.generate(targetClass, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithEnum() throws IllegalAccessException
	{
		testGenerateRunner(TestEnum.class, TestEnum.FIRST);
	}

	@Test
	public void testDefaultGenerateWithDateTime() throws IllegalAccessException
	{
		Date expectedResult = Date.from(Instant.now());

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentDate = () -> expectedResult;

		Object actualResult = target.generate(Date.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithInstant() throws IllegalAccessException
	{
		Instant expectedResult = Instant.now();

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentInstant = () -> expectedResult;

		Object actualResult = target.generate(Instant.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithZonedDateTime() throws IllegalAccessException
	{
		ZonedDateTime expectedResult = ZonedDateTime.now(ZoneOffset.UTC);

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentZonedDateTime = () -> expectedResult;

		Object actualResult = target.generate(ZonedDateTime.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testDefaultGenerateWithLocalDateTime() throws IllegalAccessException
	{
		LocalDateTime expectedResult = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();

		FabricatorConfiguration target = new FabricatorConfiguration();
		target.currentLocalDateTime = () -> expectedResult;

		Object actualResult = target.generate(LocalDateTime.class, null);

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testGenerateWithCustomGeneratorEnumFunc() throws IllegalAccessException
	{
		FabricatorConfiguration target = new FabricatorConfiguration();
		target.customGenerators.put(TestEnum.class, () -> TestEnum.THIRD);

		Object actualResult = target.generate(TestEnum.class, null);

		assertEquals(TestEnum.THIRD, actualResult);
	}

	@Test
	public void testGenerateWithCustomOverrideGeneratorIntFunc() throws IllegalAccessException
	{
		int expectedValue = 9999;
		FabricatorConfiguration target = new FabricatorConfiguration();
		target.customGenerators.put(int.class, () -> expectedValue);

		Object actualResult = target.generate(int.class, null);

		assertEquals(expectedValue, actualResult);
	}

	@Test
	public void testGenerateWithStringFieldName() throws IllegalAccessException
	{
		FabricatorConfiguration target = new FabricatorConfiguration();
		String fieldName = "myFieldName";

		Object actualResult = target.generate(String.class, fieldName);

		assertEquals(fieldName, actualResult);
	}

	@Test
	public void testGenerateWithInterface() throws IllegalAccessException
	{

		FabricatorConfiguration target = new FabricatorConfiguration();

		Object actualResult = target.generate(TestInterface.class, null);

		assertEquals(null, actualResult);
	}

	@Test
	public void testGenerateWithRecursiveFieldGeneration() throws IllegalAccessException
	{
		FabricatorConfiguration target = new FabricatorConfiguration();

		TestClassWithObjectField actualResult = (TestClassWithObjectField) target.generate(TestClassWithObjectField.class, null);

		assertNotNull(actualResult);

		assertNotNull(actualResult.classField);

		assertEquals(actualResult.generation, 1);
		assertEquals(actualResult.classField.name, "name");
		assertEquals(actualResult.classField.age, 2);
	}

	@Test
	public void testGenerateRecursiveLimit() throws IllegalAccessException
	{
		FabricatorConfiguration target = new FabricatorConfiguration();

		TestClassRecursiveLimit actualResult = (TestClassRecursiveLimit) target.generate(TestClassRecursiveLimit.class, null);

		Action2<TestClassRecursiveLimit, Integer> checkInnerClass = (inner, generation) -> {
			assertNotNull(inner);
			assertEquals(inner.generation, generation.intValue());
		};

		checkInnerClass.action(actualResult, 1);
		checkInnerClass.action(actualResult.innerObject, 2);
		checkInnerClass.action(actualResult.innerObject.innerObject, 3);
		checkInnerClass.action(actualResult.innerObject.innerObject.innerObject, 4);
		checkInnerClass.action(actualResult.innerObject.innerObject.innerObject.innerObject, 5);

		assertNull(actualResult.innerObject.innerObject.innerObject.innerObject.innerObject);
	}

	@Test
	public void testGenerateWithAbstractClasses() throws IllegalAccessException
	{
		FabricatorConfiguration target = new FabricatorConfiguration();

		TestAbstractClass actualResult = (TestAbstractClass) target.generate(TestAbstractClass.class, null);

		assertNull(actualResult);
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
		public int generation;
		TestClass classField;
	}

	public class TestClassRecursiveLimit
	{
		int generation;
		TestClassRecursiveLimit innerObject;
	}

	public abstract class TestAbstractClass
	{
	}
}