package io.narayana.txdemo.demos.remote;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import org.jboss.as.quickstarts.ejb.remote.stateful.RemoteCounter;
import org.jboss.as.quickstarts.ejb.remote.stateless.RemoteCalculator;
import io.narayana.txdemo.DummyEntity;

/**
 * Basically the same demo as {@link RemoteEjbOneClientOneServerDemo} but with persisting the counter state.
 * @author mzezulka
 *
 */
public class RemoteEjbTwoClientOneServerDemo extends RemoteEjbDemo {

    public RemoteEjbTwoClientOneServerDemo() {
        super(37, "Remote EJB client call 2/1",
                "Remote EJB client call. Requires a running server which will perform the actual remote call. Enlists two XAResource on the client and one on the server.");
    }

    protected Long dbSave(EntityManager em, DummyEntity quickstartEntity) {
        if (quickstartEntity.isTransient()) {
            em.persist(quickstartEntity);
        } else {
            em.merge(quickstartEntity);
        }
        return quickstartEntity.getId();
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
            int cntr = statefulRemoteCounter.getCount();
            LOG.debug("Count after increment is " + cntr);
            dbSave(em, new DummyEntity("Counter state: " + cntr));
        }
        LOG.debug("Counter will now be decremented " + NUM_TIMES + " times");
        for (int i = NUM_TIMES; i > 0; i--) {
            LOG.debug("Decrementing counter");
            statefulRemoteCounter.decrement();
            int cntr = statefulRemoteCounter.getCount();
            dbSave(em, new DummyEntity("Counter state: " + cntr));
        }
    }

    
    @Override
    protected void invokeStatelessBean(TransactionManager tm, EntityManager em) {
        final RemoteCalculator calculator = lookupRemoteStatelessBean("CalculatorBean", RemoteCalculator.class);
        LOG.debug("Obtained a remote stateless calculator for invocation");
        dbSave(em, new DummyEntity("Sum: " + sumTwoNumbers(calculator)));
        dbSave(em, new DummyEntity("Difference: " + subtractTwoNumbers(calculator)));
    }
    
    private int sumTwoNumbers(RemoteCalculator calculator) {
        int a = 204;
        int b = 340;
        LOG.debug("Adding " + a + " and " + b + " via the remote stateless calculator deployed on the server");
        int sum = calculator.add(a, b);
        LOG.debug("Remote calculator returned sum = " + sum);
        if (sum != a + b) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect sum " + sum + " ,expected sum was "
                + (a + b));
        }
        return sum;
    }
    
    private int subtractTwoNumbers(RemoteCalculator calculator) {
        int num1 = 3434;
        int num2 = 2332;
        LOG.debug("Subtracting " + num2 + " from " + num1
            + " via the remote stateless calculator deployed on the server");
        int difference = calculator.subtract(num1, num2);
        LOG.debug("Remote calculator returned difference = " + difference);
        if (difference != num1 - num2) {
            throw new RuntimeException("Remote stateless calculator returned an incorrect difference " + difference
                + ", expected difference was " + (num1 - num2));
        }
        return difference;
    }
}
