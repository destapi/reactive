# Data object Observer

__Reactive__ is a tiny utility library for transforming any data class into an observable data object, through the use
of an __Observer__ implementation. This feature is in a lot of ways similar to the __Proxy__ object in __JavaScript__,
for those who may be familiar with it.

This functionality can be extended to load transformed bytecode during the pre-main phase of a java program, or even to
generate class files during the build phase (with gradle or maven) through use of plugins. There are currently no
plugins created yet for the build tools.

The example shown here illustrates using this utility in standalone mode in a java program.

## Example data class

This class is used in the application to shuttle data back and forth, and it is this data whose values a user may want
to keep track of.

```java
public class Data {

    private String name;
    private boolean verified;

    public Data() {
    }

    public Data(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
```

## Example of Observer instance

This object is created by the user to receive data events whenever setters or getters in the data object of interest are
accessed.

```java
Observer watch = new Observer() {

    @Override
    public void set(Object source, String field, Object oldValue, Object newValue) {
        System.out.printf("Setting value of property '%s' in class '%s' from %s to %s\n", field,
                source.getClass().getName(), oldValue, newValue);
    }

    @Override
    public void get(Object source, String field, Object value) {
        System.out.printf("getting value of property '%s' in class '%s' with value %s\n", field,
                source.getClass().getName(), value);
    }
}
```

## Example of transformation

The transformation of a class into a participating observable is done be passing the __target class__ and __Observer__
instance to the static __observe__ method of the __Reactive__ class.

```java
public class RunObserver {

    public static void main(String[] args) throws IOException {
        generateRuntime();
    }

    public static void generateRuntime() throws IOException {
        List<Data> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Data newData = Reactive.observe(Data.class, watch);
            newData.setName("Stephano_" + i);
            newData.setVerified(true);
            list.add(newData);
        }
        list.forEach(a -> System.out.printf("Is %s verified? %s", a.getName(), a.isVerified()));
    }
}
```

## Output generated by the example above

```bash
Setting value of property 'name' in class 'com.akilisha.reactive.data.Data0' from null to Stephano_0
Setting value of property 'verified' in class 'com.akilisha.reactive.data.Data0' from false to true
Setting value of property 'name' in class 'com.akilisha.reactive.data.Data0' from null to Stephano_1
Setting value of property 'verified' in class 'com.akilisha.reactive.data.Data0' from false to true
Setting value of property 'name' in class 'com.akilisha.reactive.data.Data0' from null to Stephano_2
Setting value of property 'verified' in class 'com.akilisha.reactive.data.Data0' from false to true
getting value of property 'name' in class 'com.akilisha.reactive.data.Data0' with value Stephano_0
getting value of property 'verified' in class 'com.akilisha.reactive.data.Data0' with value true
Is Stephano_0 verified? truegetting value of property 'name' in class 'com.akilisha.reactive.data.Data0' with value Stephano_1
getting value of property 'verified' in class 'com.akilisha.reactive.data.Data0' with value true
Is Stephano_1 verified? truegetting value of property 'name' in class 'com.akilisha.reactive.data.Data0' with value Stephano_2
getting value of property 'verified' in class 'com.akilisha.reactive.data.Data0' with value true
Is Stephano_2 verified? true
```

## Generating the observable class file

This can optionally be done during the build phase of a java application so that the class files may be loaded by the
default application classloader during normal startup.

```java
public class GenObserver {

    public static void main(String[] args) throws IOException {
        generateToFile(Reactive.defaultBuildPath, String.valueOf(LocalTime.now().getSecond()));
    }

    public static void generateToFile(String buildPath, String discriminator) {
        Reactive.generate(Data.class, "app/" + buildPath, discriminator);
    }
}
```

## Extending the idea further

This idea of the Observer utility can be extrapolated further and used for example, to generate data events, which would
effectively create a data stream emanating from data changing in the observed instances. This can be illustrated easily
using RXjava's Observable object.

```java 
class SomeEventPipeline {

    Observer watch;

    Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {

        @Override
        public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
            watch = new Observer() {

                @Override
                public void set(Object source, String field, Object oldValue, Object newValue) {
                    emitter.onNext("updating value: " + newValue);
                }

                @Override
                public void get(Object source, String field, Object value) {
                    emitter.onNext("accessing value: " + value);
                }
            };
        }
    });

    public Observer getObserver() {
        return Objects.requireNonNull(watch, "You have to subscribed to Observable first before this step");
    }

    public Observable<String> getObservable() {
        return observable;
    }
}
```

Once the __RXjava__ Observable has been set up, the __Observer__ can then be passed on to any number of data objects
whose data is of interest. In this example, the data change made to the data object is simulated using keyboard input.
This data change can be triggered by any other means imaginable.

```java
public class DataSimulate {

    public static void main(String[] args) throws IOException {
        // something in the application which produces events
        SomeEventPipeline gen = new SomeEventPipeline();

        // subscribing to the event source
        gen.getObservable().subscribe(next -> {
            System.out.println("Event generated: " + next);
        });

        // creating an observable data object
        Data data = Reactive.observe(Data.class, gen.getObserver());

        // Updating values in data object will generate events in the pipeline
        // keyboard input is used to make the illustration here, but the data changes could be as a result of many other diffrent reasons
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                data.setName(line);         // updating data value
                data.getName();             // accessing data value
            }
        }
    }
}
```

The output generated by this example will look like shown below

```bash
one
Event generated: updating value: one
Event generated: accessing value: one
two
Event generated: updating value: two
Event generated: accessing value: two
three
Event generated: updating value: three
Event generated: accessing value: three
```

## Summary

The sky is truly the limit for what anyone can do with this power! If you run into a problem with the utility or
envision a usage scenarion that would be nice to have, please open a GitHub issue to kick off a conversaton.