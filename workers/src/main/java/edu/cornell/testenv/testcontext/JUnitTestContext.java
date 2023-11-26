package edu.cornell.testenv.testcontext;

import java.util.ArrayList;
import java.util.List;

public class JUnitTestContext implements TestEnvContext<String> {

    List<String> classNames;

    public JUnitTestContext(List<String> classNames) {
        this.classNames = classNames;
    }

    public List<String> getTestClasses() {
        return classNames;
    }

}
