package com.orbitz.monitoring.api.monitor;

import com.orbitz.monitoring.api.MonitoringEngine;
import com.orbitz.monitoring.api.MonitoringLevel;
import com.orbitz.monitoring.api.Attribute;

import java.util.Map;
import java.util.Date;

/**
 * A monitor for transactions. Transactions implicitly have durations. In order
 * to prevent instrumentation errors from showing failing transactions as
 * successful, TransactionMonitors assume that a transaction failed unless
 * success is called.
 *
 * @author Doug Barth
 */
public class TransactionMonitor extends AbstractCompositeMonitor {
    /**
     * @deprecated use Attribute.RESULT_CODE instead
     */
    public static final String RESULT_CODE = Attribute.RESULT_CODE;

    /**
     * @deprecated use Attribute.START_TIME instead
     */
    protected static final String START_TIME = Attribute.START_TIME;
    /**
     * @deprecated use Attribute.END_TIME instead
     */
    protected static final String END_TIME = Attribute.END_TIME;
    /**
     * @deprecated use Attribute.LATENCY instead
     */
    protected static final String LATENCY = Attribute.LATENCY;

    /**
     * @deprecated use Attribute.FAILURE_THROWABLE instead
     */
    protected static final String FAILURE_THROWABLE = Attribute.FAILURE_THROWABLE;
    /**
     * @deprecated use Attribute.FAILED instead
     */
    protected static final String FAILED = Attribute.FAILED;
    /**
     * @deprecated use Attribute.BUSINESS_FAILURE instead
     */
    protected static final String BUSINESS_FAILURE = Attribute.BUSINESS_FAILURE;
    /**
     * @deprecated use Attribute.TRANSACTION_MONITOR instead
     */
    protected static final String TRANSACTION_MONITOR = Attribute.TRANSACTION_MONITOR;

    /**
     * Creates a new TransactionMonitor with the given name. The monitor is
     * marked as failed by default. Also, the start time of this transaction is
     * noted, thereby starting the stop watch.
     *
     * @param name the name of this transaction
     */
    public TransactionMonitor(String name) {
        super(name);

        startTransactionMonitor();
    }

    /**
     * Creates a new TransactionMonitor with the given name and level.
     *
     * @param name the name of this transaction
     * @param monitoringLevel monitoring level
     */
    public TransactionMonitor(String name, MonitoringLevel monitoringLevel) {
        super(name, monitoringLevel);

        startTransactionMonitor();
    }

    /**
     * Creates a new TransactionMonitor with the given name and default
     * attributes. The transaction is marked failed by default. The start time
     * is also noted.
     *
     * @param name the name of this transaction
     * @param defaultAttributes attributes that should be set on this
     *        monitor immediately
     */
    public TransactionMonitor(String name, Map defaultAttributes) {
        super(name, defaultAttributes);

        startTransactionMonitor();
    }

    /**
     * Creates a new TransactionMonitor with the given name, level and default
     * attributes. The transaction is marked failed by default. The start time
     * is also noted.
     *
     * @param name the name of this transaction
     * @param monitoringLevel monitoring level
     * @param defaultAttributes attributes that should be set on this
     *        monitor immediately
     */
    public TransactionMonitor(String name, MonitoringLevel monitoringLevel, Map defaultAttributes) {
        super(name, monitoringLevel, defaultAttributes);

        startTransactionMonitor();
    }

    /**
     * Creates a new TransactionMonitor with a name obtained by concatenating
     * the class name and method string together.
     *
     * @param klass the class that we're monitoring
     * @param method a string containing the method name that we're monitoring
     */
    public TransactionMonitor(Class klass, String method) {
        this(formatName(klass, method));
    }

    /**
     * Creates a new TransactionMonitor with a name obtained by concatenating
     * the class name and method string together, with the given monitoring level
     *
     * @param klass the class that we're monitoring
     * @param method a string containing the method name that we're monitoring
     * @param level monitoring level
     */
    public TransactionMonitor(Class klass, String method, MonitoringLevel level) {
        this(formatName(klass, method), level);
    }

    /**
     * Creates a new TransactionMonitor with default attributes and a name
     * composed of the class name and method name.
     *
     * @param klass the class that we're monitoring
     * @param method a string containing the method name that we're monitoring
     * @param defaultAttributes the default attributes for this monitor
     */
    public TransactionMonitor(Class klass, String method, Map defaultAttributes) {
        this(formatName(klass, method), defaultAttributes);
    }

    // ** PUBLIC METHODS ******************************************************
    /**
     * Marks this transaction as having succeeded.
     */
    public void succeeded() {
        set(Attribute.FAILED, false);
    }

    /**
     * Marks this transaction as having failed.
     */
    public void failed() {
        set(Attribute.FAILED, true);
    }

    /**
     * Marks this transaction as having failed due to the supplied Throwable.
     *
     * @param e the Throwable that caused the failure
     */
    public void failedDueTo(Throwable e) {
        set(Attribute.FAILURE_THROWABLE, e).serializable();
        failed();
    }

    // ** PROTECTED METHODS ***************************************************
    
    /**
     * Stops the stop watch for this monitor and submits it for processing.
     */
    public void done() {
        Date endTime = new Date();
        set(Attribute.END_TIME, endTime).serializable().lock();

        Date startTime = (Date) get(Attribute.START_TIME);
        set(Attribute.LATENCY, endTime.getTime() - startTime.getTime()).serializable().lock();

        process();
    }

    // ** PRIVATE METHODS *****************************************************
    private static String formatName(Class klass, String method) {
        return klass.getName() + "." + method;
    }

    private void startTransactionMonitor() {
        //if (MonitoringEngine.getInstance().isEnabled()) assert hasAttribute(CREATED_AT);

        set(Attribute.FAILED, true).serializable();
        set(Attribute.START_TIME, new Date()).serializable().lock();

        MonitoringEngine.getInstance().monitorStarted(this);
    }
}
