# Fabrication [![The build status of this project](https://api.travis-ci.org/ArtisanCode/Fabrication.svg "Fabrication build status")](https://travis-ci.org/ArtisanCode/Fabrication)
A Java testing library that helps fabricate classes with test data.

Given the following class:

```
public class TestObject
{
	private String name;
	private int age;

	// Getters and setters...
}
```

You can quickly and easily create in instance to test with by using the Fabricator:

```
TestObject result = new Fabricator<TestObject>()
				        .createNew(TestObject.class)
				        .with(x -> x.setName("Tom"))
				        .fabricate();
```

This will create a test object with the name set as "Tom" and the age set as 0