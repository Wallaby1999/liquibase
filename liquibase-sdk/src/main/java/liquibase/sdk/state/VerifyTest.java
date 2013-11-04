package liquibase.sdk.state;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.*;

import static org.junit.Assert.fail;

public class VerifyTest extends TestWatcher {
    private Class<?> testClass;
    private String testName;
    private String displayName;
    private List<Verification> checks;
    private List<Setup> setupCommands;
    private SortedMap<String, Value> permutationDefinition;
    private SortedMap<String, Value> info;
    private SortedMap<String, Value> data;

//    @Override
    protected void starting(Description description) {
        this.testClass = description.getTestClass();
        this.testName = description.getMethodName();
        this.displayName = description.getDisplayName();

        this.info = new TreeMap<String, Value>();
        this.permutationDefinition = new TreeMap<String, Value>();
        this.data = new TreeMap<String, Value>();
        this.checks = new ArrayList<Verification>();
        this.setupCommands = new ArrayList<Setup>();
    }

    @Override
    protected void succeeded(Description description) {
        if (permutationDefinition.size() > 0 || data.size() > 0) {
            if (permutationDefinition.size() == 0) {
                fail("Did not define any test permutation definitions to identify this permutation");
            }

            PersistedTestResults persistedTestResults = PersistedTestResults.getInstance(testClass, testName);

            persistedTestResults.runTest(this);
        }
    }

    @Override
    protected void failed(Throwable e, Description description) {
        PersistedTestResults.getInstance(testClass, testName).markFailed();
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public String getTestName() {
        return testName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SortedMap<String, Value> getPermutationDefinition() {
        return Collections.unmodifiableSortedMap(permutationDefinition);
    }

    public void addPermutationDefinition(String message, Object value) {
        this.addPermutationDefinition(message, value, OutputFormat.DefaultFormat);
    }

    public void addPermutationDefinition(String message, Object value, OutputFormat format) {
        this.permutationDefinition.put(message, new Value(value, format));
    }

    public SortedMap<String, Value> getInfo() {
        return Collections.unmodifiableSortedMap(info);
    }

    public void addInfo(String message, Object value) {
        this.addInfo(message, value, OutputFormat.DefaultFormat);
    }

    public void addInfo(String message, Object value, OutputFormat format) {
        this.info.put(message, new Value(value, format));
    }

    public void addOutput(String message, String value) {
        this.addData(message, value, OutputFormat.DefaultFormat);
    }

    public void addData(String message, Object value, OutputFormat outputFormat) {
        this.data.put(message, new Value(value, outputFormat));
    }

    public SortedMap<String, Value> getData() {
        return Collections.unmodifiableSortedMap(data);
    }

    public void setup(Setup setup) {
        this.setupCommands.add(setup);
    }

    public void verifyChanges(Verification check) {
        this.checks.add(check);
    }

    public List<Verification> getChecks() {
        return checks;
    }

    public List<Setup> getSetupCommands() {
        return setupCommands;
    }

    public static class Value {
        private Object value;
        private OutputFormat format;

        public Value(Object value, OutputFormat format) {
            this.value = value;
            this.format = format;
        }

        public String serialize() {
            return format.format(value);
        }
    }
}
