package edu.cornell.testenv.testrunner;

import edu.cornell.testenv.testcontext.TestEnvContext;

public class JUnitTestRunner implements TestRunner {

    @Override
    public boolean runTest(TestEnvContext envInput) {
        try {
            Request request = Request.aClass(envInput.getTestClass());

            JUnitCore jUnitCore = new JUnitCore();
            Result res = jUnitCore.run(request);

            for (Failure failure : res.getFailures()) {
                System.out.println(failure.toString());
            }

            return res.wasSuccessful();
        } catch (Throwable t) {
            return true;
        }
    }
}
