package io.narayana.txdemo.demos.remote;
import javax.ejb.Stateless;
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
@Stateless
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
        final RemoteCounter counter = lookupRemoteStatefulBean("CounterBean", RemoteCounter.class);
        LOG.debug("Obtained a remote stateful counter for invocation");
        dbSave(em, new DummyEntity("Counter state after increment: " + incrementCounter(counter)));
        dbSave(em, new DummyEntity("Counter state after decrement: " + decrementCounter(counter)));
    }

    
    @Override
    protected void invokeStatelessBean(TransactionManager tm, EntityManager em) {
        final RemoteCalculator calculator = lookupRemoteStatelessBean("CalculatorBean", RemoteCalculator.class);
        LOG.debug("Obtained a remote stateless calculator for invocation");
        dbSave(em, new DummyEntity("Sum: " + sumTwoNumbers(calculator)));
        dbSave(em, new DummyEntity("Difference: " + subtractTwoNumbers(calculator)));
    }
}
