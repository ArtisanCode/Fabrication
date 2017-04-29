package com.artisancode.fabrication;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CollectionBuilder<T> {
    protected Class<? extends T> target;
    protected FabricatorConfiguration configuration;
    protected int size;
    protected CollectionModifications state;
    protected int primaryOperationModifier;
    protected int secondaryOperationModifier;
    protected int lastModificationStartIndex = 0;
    protected int lastModificationEndIndex = 0;
    protected List<List<Consumer<T>>> modificationsArray;
    protected Predicate<Integer> operationPredicate;
    protected Random random;
    protected Supplier<Integer> getRandomIndex = () -> random.nextInt(size);
    protected Map<CollectionModifications, Consumer<Consumer<T>>> stateModificationsMap = new HashMap<>();

    public CollectionBuilder(Class<? extends T> target) {
        this(target, new FabricatorConfiguration());
    }

    public CollectionBuilder(Class<? extends T> target, FabricatorConfiguration configuration) {
        this.target = target;
        this.configuration = configuration;
        state = CollectionModifications.ALL;

        stateModificationsMap = new HashMap<>();
        stateModificationsMap.put(CollectionModifications.ALL, (mod) -> handleGlobalModifications(mod));
        stateModificationsMap.put(CollectionModifications.FIRST, (mod) -> handleFirstModifications(mod));
        stateModificationsMap.put(CollectionModifications.NEXT, (mod) -> handleNextModifications(mod));
        stateModificationsMap.put(CollectionModifications.LAST, (mod) -> handleLastModifications(mod));
        stateModificationsMap.put(CollectionModifications.PREVIOUS, (mod) -> handlePreviousModifications(mod));
        stateModificationsMap.put(CollectionModifications.NTH, (mod) -> handleModifyTheNthElement(mod));
        stateModificationsMap.put(CollectionModifications.SLICE, (mod) -> handleSliceModifications(mod));
        stateModificationsMap.put(CollectionModifications.PREDICATE, (mod) -> handlePredicatedModifications(mod));
        stateModificationsMap.put(CollectionModifications.RANDOM, (mod) -> handleRandomModifications(mod));

        initModificationsAndState(5); // Default list size is 5
    }

    public CollectionBuilder<T> ofSize(int size) {
        initModificationsAndState(size);
        return this;
    }

    protected void initModificationsAndState(int size) {
        this.size = size;
        modificationsArray = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            modificationsArray.add(i, new LinkedList<>());
        }

        // Set the initial state of the CollectionBuilder so that you can't use previous/next without other modifications
        lastModificationStartIndex = 0;
        lastModificationEndIndex = size - 1;
    }

    public CollectionBuilder<T> all() {
        state = CollectionModifications.ALL;
        return this;
    }

    public CollectionBuilder<T> and(Consumer<T> modifier) {
        return add(modifier);
    }

    public CollectionBuilder<T> with(Consumer<T> modifier) {
        return add(modifier);
    }

    public CollectionBuilder<T> theFirst(int number) {
        return configureState(CollectionModifications.FIRST, number);
    }

    public CollectionBuilder<T> theNext(int number) {
        return configureState(CollectionModifications.NEXT, number);
    }

    public CollectionBuilder<T> theLast(int number) {
        return configureState(CollectionModifications.LAST, number);
    }

    public CollectionBuilder<T> thePrevious(int number) {
        return configureState(CollectionModifications.PREVIOUS, number);
    }

    public CollectionBuilder<T> theNth(int number) {
        return configureState(CollectionModifications.NTH, number);
    }

    public CollectionBuilder<T> predicated(Predicate<Integer> predicate) {
        return configureState(CollectionModifications.PREDICATE, predicate);
    }

    public CollectionBuilder<T> theSlice(int start, int end) {
        return configureState(CollectionModifications.SLICE, start, end);
    }

    public CollectionBuilder<T> random(int number) {
        random = new Random((int) LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toEpochSecond());
        return configureState(CollectionModifications.RANDOM, number);
    }

    public CollectionBuilder<T> random(int number, int seed) {
        random = new Random(seed);
        return configureState(CollectionModifications.RANDOM, number);
    }

    public List<T> fabricate() {
        T[] result = (T[]) new Object[size];

        ObjectBuilder<T> builder = new ObjectBuilder<T>(target, configuration);

        for (int i = 0; i < size; i++) {
            T fabricatedObject = builder.fabricate();
            List<Consumer<T>> modifications = modificationsArray.get(i);
            for (Consumer<T> mod : modifications) {
                mod.accept(fabricatedObject);
            }

            result[i] = fabricatedObject;
        }

        return Arrays.asList(result);
    }

    protected CollectionBuilder<T> add(Consumer<T> modifier) {
        if (stateModificationsMap.containsKey(state)) {
            stateModificationsMap.get(state).accept(modifier);
        }
        return this;
    }

    private void handleRandomModifications(Consumer<T> modifier) {
        if (primaryOperationModifier <= 0) {
            throw new FabricationException(String.format("Unable to modify %d number of random elements as the number of nodes to affect needs to be a positive integer", primaryOperationModifier));
        }

        if (primaryOperationModifier > size) {
            throw new FabricationException(String.format("Unable to modify %d number of random elements as the number of nodes to affect needs to less than or equal to the total size of the collection %d", primaryOperationModifier, size));
        }

        Set<Integer> indiciesToModify = new HashSet<>(size);

        while (indiciesToModify.size() != primaryOperationModifier) {
            indiciesToModify.add(getRandomIndex.get());
        }

        for (Integer index : indiciesToModify) {
            modificationsArray.get(index).add(modifier);
        }
    }

    protected void handlePredicatedModifications(Consumer<T> modifier) {
        boolean modified = false;
        for (int i = 0; i < modificationsArray.size(); i++) {
            // If the index matches the predicate, then add the modifier
            if (operationPredicate.test(i)) {
                if (!modified) {
                    // Only set the start index state the first time we set a modifier
                    modified = true;
                    lastModificationStartIndex = i;
                }

                modificationsArray.get(i).add(modifier);
                lastModificationEndIndex = i;
            }
        }
    }

    protected void handleModifyTheNthElement(Consumer<T> modifier) {
        if (primaryOperationModifier < 0) {
            throw new FabricationException(String.format("Unable to modify the nth element %d as the index to affect needs to be a positive integer or zero", primaryOperationModifier));
        }

        if (primaryOperationModifier >= size) {
            throw new FabricationException(String.format("Unable to modify the nth element %d as the index to affect needs to less than the total size of the collection %d", primaryOperationModifier, size));
        }

        modifySlice(modifier, primaryOperationModifier, primaryOperationModifier);
    }

    protected void handleSliceModifications(Consumer<T> modifier) {
        if (primaryOperationModifier < 0) {
            throw new FabricationException(String.format("Unable to modify the start of the slice at %d as the start index needs to be a positive integer or zero", secondaryOperationModifier));
        }

        if (primaryOperationModifier > size - 2) {
            throw new FabricationException(String.format("Unable to modify the start of the slice at %d as the start index needs to be two less than the size of the collection", secondaryOperationModifier));
        }

        if (secondaryOperationModifier < 0) {
            throw new FabricationException(String.format("Unable to modify the end of the slice at %d as the end index needs to be a positive integer", secondaryOperationModifier));
        }

        if (secondaryOperationModifier >= size) {
            throw new FabricationException(String.format("Unable to slice as the end index %d is greater than the size of the collection %d", secondaryOperationModifier, size));
        }

        if (primaryOperationModifier == secondaryOperationModifier) {
            throw new FabricationException(String.format("Unable to slice as the start and end index are the same (%d)... Did you mean to use operation: theNth?", secondaryOperationModifier));
        }

        modifySlice(modifier, primaryOperationModifier, secondaryOperationModifier);
    }

    protected void handleLastModifications(Consumer<T> modifier) {
        if (primaryOperationModifier < 1) {
            throw new FabricationException(String.format("Unable to modify the last %d elements as the number of elements to affect needs to be a positive integer", primaryOperationModifier));
        }

        if (primaryOperationModifier > size) {
            throw new FabricationException(String.format("Unable to modify the last %d elements as the list is only of size %d", primaryOperationModifier, size));
        }

        modifySlice(modifier, size - primaryOperationModifier, size - 1);
    }

    public void handleGlobalModifications(Consumer<T> modifier) {
        for (List<Consumer<T>> modifiers : modificationsArray) {
            modifiers.add(modifier);
        }
    }

    public void handleFirstModifications(Consumer<T> modifier) {
        if (primaryOperationModifier < 1) {
            throw new FabricationException(String.format("Unable to modify the first %d elements as the number of elements to affect needs to be a positive integer", primaryOperationModifier));
        }

        if (primaryOperationModifier > size) {
            throw new FabricationException(String.format("Unable to modify the first %d elements as the list is only of size %d", primaryOperationModifier, size));
        }

        modifySlice(modifier, 0, primaryOperationModifier - 1);
    }

    public void handleNextModifications(Consumer<T> modifier) {
        int start = lastModificationStartIndex + 1;
        int end = start + primaryOperationModifier;

        if (primaryOperationModifier < 1) {
            throw new FabricationException(String.format("Unable to modify the next %d elements as the number of elements to affect needs to be a positive integer", primaryOperationModifier));
        }

        if (end > size) {
            throw new FabricationException(String.format("Unable to modify the next %d elements as the list only has %d elements left", primaryOperationModifier, size - start));
        }

        modifySlice(modifier, start, end);
    }

    public void handlePreviousModifications(Consumer<T> modifier) {
        int end = lastModificationStartIndex - 1;
        int start = end - primaryOperationModifier;

        if (primaryOperationModifier < 1) {
            throw new FabricationException(String.format("Unable to modify the previous %d elements as the number of elements to affect needs to be a positive integer", primaryOperationModifier));
        }

        if (end > size) {
            throw new FabricationException(String.format("Unable to modify the next %d elements as the list only has %d elements left", primaryOperationModifier, size - start));
        }

        modifySlice(modifier, start, end);
    }

    protected void modifySlice(Consumer<T> modifier, int start, int end) {
        lastModificationStartIndex = start;
        lastModificationEndIndex = end;

        for (int i = lastModificationStartIndex; i <= lastModificationEndIndex; i++) {
            modificationsArray.get(i).add(modifier);
        }
    }

    private CollectionBuilder<T> configureState(CollectionModifications modification, int operationModifier) {
        state = modification;
        this.primaryOperationModifier = operationModifier;
        return this;
    }

    private CollectionBuilder<T> configureState(CollectionModifications modification, Predicate<Integer> predicate) {
        state = modification;
        operationPredicate = predicate;
        return this;
    }

    private CollectionBuilder<T> configureState(CollectionModifications modification, int start, int end) {
        state = modification;
        primaryOperationModifier = start;
        secondaryOperationModifier = end;
        return this;
    }

    private enum CollectionModifications {
        ALL,
        FIRST,
        NEXT,
        LAST,
        PREVIOUS,
        NTH,
        SLICE,
        PREDICATE,
        RANDOM
    }
}
