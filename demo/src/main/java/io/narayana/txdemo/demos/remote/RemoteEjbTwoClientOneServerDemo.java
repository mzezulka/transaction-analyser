package io.narayana.txdemo.demos.remote;

public class RemoteEjbTwoClientOneServerDemo extends RemoteEjbDemo {

    public RemoteEjbTwoClientOneServerDemo() {
        super(37, "Remote EJB client call 2/1",
                "Remote EJB client call. Requires a running server which will perform the actual remote call. Enlists two XAResource on the client and one on the server.");
    }

    @Override
    protected void invokeStatefulBean() {
        
    }

    @Override
    protected void invokeStatelessBean() {
        
    }

}
