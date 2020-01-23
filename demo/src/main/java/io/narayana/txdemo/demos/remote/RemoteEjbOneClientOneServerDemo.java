package io.narayana.txdemo.demos.remote;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.jboss.as.quickstarts.ejb.remote.stateful.RemoteCounter;
import org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator;

@Stateless
public class RemoteEjbOneClientOneServerDemo extends RemoteEjbDemo {

    public RemoteEjbOneClientOneServerDemo() {
		super(35, "Remote EJB client call 1/1",
				"Remote EJB client call. Requires a running server which will perform the actual remote call. Enlists one XAResource on both the client and the server.");
	}
    
    @Override
    protected void invokeStatefulBean(TransactionManager tm, EntityManager em) {
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
    protected void invokeStatelessBean(TransactionManager tm, EntityManager em) {
        final RemoteCalculator calculator = lookupRemoteStatelessBean("CalculatorBean", RemoteCalculator.class);
        LOG.debug("Obtained a remote stateless calculator for invocation");
        sumTwoNumbers(calculator);
        subtractTwoNumbers(calculator);
    }
}
