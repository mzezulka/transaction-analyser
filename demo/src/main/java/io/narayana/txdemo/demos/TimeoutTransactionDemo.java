/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package io.narayana.txdemo.demos;

import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;

import io.narayana.txdemo.DemoResult;
import io.narayana.txdemo.xaresources.DummyXAResource;

/**
 * @author <a href="mailto:zfeng@redhat.com">Amos Feng</a>
 */
public class TimeoutTransactionDemo extends Demo {

    public TimeoutTransactionDemo() {

        super(2, "Transaction Timeout", "This demo sets the transaction timeout to 1 second and then sleeps for 2 seconds " +
                "after the transaction has begun. This simulates a transaction timeout due to slow business logic." +
                "The transaction outcome is TIMEOUT");
    }

    @Override
    public DemoResult run(TransactionManager tm, EntityManager em) throws Exception {


        tm.setTransactionTimeout(1);
        tm.begin();

        tm.getTransaction().enlistResource(new DummyXAResource("demo1"));
        tm.getTransaction().enlistResource(new DummyXAResource("demo2"));
        create(em, "timeout");
        Thread.sleep(2000);

        tm.commit();

        return new DemoResult(-1, "should throw rollback exception");
    }
}
