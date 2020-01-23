package io.narayana.txdemo.demos.remote;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

@Stateless
public class RemoteEjbOneClientTwoServerDemo extends RemoteEjbDemo {

    public RemoteEjbOneClientTwoServerDemo() {
        super(36, "Remote EJB client call 1/2",
                "Remote EJB client call. Requires a running server which will perform the actual remote call. Enlists one XAResource on the client and two on the server.");
    }

    @Override
    protected void invokeStatefulBean(TransactionManager tm, EntityManager em) {
        
    }

    @Override
    protected void invokeStatelessBean(TransactionManager tm, EntityManager em) {
        
    }

}
