package io.narayana.txdemo.demos.remote;

import javax.ejb.Stateless;
import org.jboss.as.quickstarts.ejb.remote.stateful.RemoteCounter;
import org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator;

@Stateless
public class RemoteEjbOneClientOneServerDemo extends RemoteEjbDemo {

    public RemoteEjbOneClientOneServerDemo() {
		super(35, "Remote EJB client call 1/1",
				"Remote EJB client call. Requires a running server which will perform the actual remote call. Enlists one XAResource on both the client and the server.");
	}
    
    @Override
    protected void invokeStatefulBean() {
        final RemoteCounter statefulRemoteCounter = lookupRemoteStatefulBean("CounterBean", RemoteCounter.class);
        LOG.debug("Obtained a remote stateful counter for invocation");
        final int NUM_TIMES = 1;
        LOG.debug("Counter will now be incremented " + NUM_TIMES + " times");
        for (int i = 0; i < NUM_TIMES; i++) {
            LOG.debug("Incrementing counter");
            statefulRemoteCounter.increment();
            LOG.debug("Count after increment is " + statefulRemoteCounter.getCount());
        }
        LOG.debug("Counter will now be decremented " + NUM_TIMES + " times");
        for (int i = NUM_TIMES; i > 0; i--) {
            LOG.debug("Decrementing counter");
            statefulRemoteCounter.decrement();
            LOG.debug("Count after decrement is " + statefulRemoteCounter.getCount());
        }
    }

    @Override
    protected void invokeStatelessBean() {
        final RemoteCalculator statelessRemoteCalculator = lookupRemoteStatelessBean("CalculatorBean", RemoteCalculator.class);
        LOG.debug("Obtained a remote stateless calculator for invocation");
        int a = 204;
        int b = 340;
        LOG.debug("Adding " + a + " and " + b + " via the remote stateless calculator deployed on the server");
        int sum = statelessRemoteCalculator.add(a, b);
        LOG.debug("Remote calculator returned sum = " + sum);
        if (sum != a + b) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect sum " + sum + " ,expected sum was "
                + (a + b));
        }
        int num1 = 3434;
        int num2 = 2332;
        LOG.debug("Subtracting " + num2 + " from " + num1
            + " via the remote stateless calculator deployed on the server");
        int difference = statelessRemoteCalculator.subtract(num1, num2);
        LOG.debug("Remote calculator returned difference = " + difference);
        if (difference != num1 - num2) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect difference " + difference
                + " ,expected difference was " + (num1 - num2));
        }
    }

}
