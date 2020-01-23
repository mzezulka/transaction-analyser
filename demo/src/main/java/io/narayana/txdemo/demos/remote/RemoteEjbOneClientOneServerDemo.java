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
        final RemoteCounter counter = lookupRemoteStatefulBean("CounterBean", RemoteCounter.class);
        LOG.debug("Obtained a remote stateful counter for invocation");
        incrementCounter(counter);
        decrementCounter(counter);
    }

    @Override
    protected void invokeStatelessBean(TransactionManager tm, EntityManager em) {
        final RemoteCalculator calculator = lookupRemoteStatelessBean("CalculatorBean", RemoteCalculator.class);
        LOG.debug("Obtained a remote stateless calculator for invocation");
        sumTwoNumbers(calculator);
        subtractTwoNumbers(calculator);
    }
}
