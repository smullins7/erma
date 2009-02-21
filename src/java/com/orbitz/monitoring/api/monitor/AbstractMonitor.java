package com.orbitz.monitoring.api.monitor;

import com.orbitz.monitoring.api.Monitor;
import com.orbitz.monitoring.api.MonitoringEngine;
import com.orbitz.monitoring.api.MonitoringLevel;
import com.orbitz.monitoring.api.Attribute;
import com.orbitz.monitoring.api.monitor.serializable.SerializableMonitor;
import org.apache.commons.lang.CharSetUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A base implementation of {@link Monitor} that contains all common aspects of
 * monitors.
 *
 * @author Doug Barth
 */
public abstract class AbstractMonitor implements Monitor {

    private static final Logger log = Logger.getLogger(AbstractMonitor.class);

    protected AttributeMap _attributes;
    private boolean _processed;
    protected MonitoringLevel _monitoringLevel = MonitoringLevel.INFO;

    private static final String invalidCharacters = " \\[\\]*,|()$@|~?&<>\\^";

    protected AbstractMonitor() {
        _attributes = createAttributeMap();
        MonitoringEngine.getInstance().initGlobalAttributes(this);
    }

    /**
     * This constructor is provided for subclasses that need to
     * initialize any themselves before the set() methods are invoked.
     * Subclasses that do this should use this constructor and then call init()
     *
     * @param inheritedAttributes the collection of inheritable attributes that should be
     * set on this Monitor
     * */
    protected AbstractMonitor(Map inheritedAttributes) {
        this();
        if(inheritedAttributes != null) {
            for (Iterator i = inheritedAttributes.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if(value != null && AttributeHolder.class.isAssignableFrom(value.getClass())) {
                    value = ((AttributeHolder) value).getValue();
                }
                set(key, value).lock();
            }
        }
    }

    public AbstractMonitor(String name) {
        this();
        init(name);
    }

    public AbstractMonitor(String name, MonitoringLevel monitoringLevel) {
        this();
        _monitoringLevel = monitoringLevel;
        init(name);
    }

    public AbstractMonitor(String name, MonitoringLevel monitoringLevel, Map inheritedAttributes) {
        this(inheritedAttributes);
        _monitoringLevel = monitoringLevel;
        init(name);
    }

    public AbstractMonitor(String name, Map inheritedAttributes) {
        this(name, MonitoringLevel.INFO, inheritedAttributes);
    }

    public AttributeHolder set(String key, short value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, int value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, long value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, float value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, double value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, char value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, byte value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, boolean value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, Date value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, String value) {
        return _attributes.set(key, value).serializable();
    }

    public AttributeHolder set(String key, Object value) {
        return _attributes.set(key, value);
    }

    public void setAll(Map attributes) {
        _attributes.setAll(attributes);
    }

    public void setAllAttributeHolders(final Map attributeHolders) {
        _attributes.setAllAttributeHolders(attributeHolders);
    }

    public void unset(String key) {
        _attributes.unset(key);
    }

    public Object get(String key) {
        return _attributes.get(key);
    }

    public Map getAsMap(String key) {
        return _attributes.getAsMap(key);
    }

    public List getAsList(String key) {
        return _attributes.getAsList(key);
    }

    public Set getAsSet(String key) {
        return _attributes.getAsSet(key);
    }

    public String getAsString(String key) {
        return _attributes.getAsString(key);
    }

    public short getAsShort(String key) {
        return _attributes.getAsShort(key);
    }

    public int getAsInt(String key) {
        return _attributes.getAsInt(key);
    }

    public long getAsLong(String key) {
        return _attributes.getAsLong(key);
    }

    public float getAsFloat(String key) {
        return _attributes.getAsFloat(key);
    }

    public double getAsDouble(String key) {
        return _attributes.getAsDouble(key);
    }

    public char getAsChar(String key) {
        return _attributes.getAsChar(key);
    }

    public byte getAsByte(String key) {
        return _attributes.getAsByte(key);
    }

    public boolean getAsBoolean(String key) {
        return _attributes.getAsBoolean(key);
    }

    public Map getAll() {
        return _attributes.getAll();
    }

    public Map getAllSerializable() {
        return _attributes.getAllSerializable();
    }

    public boolean getAsBoolean(String key, boolean defaultValue) {
        return _attributes.getAsBoolean(key, defaultValue);
    }

    public short getAsShort(String key, short defaultValue) {
        return _attributes.getAsShort(key, defaultValue);
    }

    public byte getAsByte(String key, byte defaultValue) {
        return _attributes.getAsByte(key, defaultValue);
    }

    public int getAsInt(String key, int defaultValue) {
        return _attributes.getAsInt(key, defaultValue);
    }

    public long getAsLong(String key, long defaultValue) {
        return _attributes.getAsLong(key, defaultValue);
    }

    public float getAsFloat(String key, float defaultValue) {
        return _attributes.getAsFloat(key, defaultValue);
    }

    public double getAsDouble(String key, double defaultValue) {
        return _attributes.getAsDouble(key, defaultValue);
    }

    public char getAsChar(String key, char defaultValue) {
        return _attributes.getAsChar(key, defaultValue);
    }

    public final MonitoringLevel getLevel() {
        MonitoringLevel overrideLevel = MonitoringEngine.getInstance().getOverrideLevelForMonitor(this);
        return (overrideLevel != null ? overrideLevel : _monitoringLevel);
    }

    public boolean hasAttribute(String key) {
        return _attributes.hasAttribute(key);
    }

    public SerializableMonitor getSerializableMomento() {
        MonitoringEngine engine = MonitoringEngine.getInstance();
        Map serializableAttributes = engine.makeAttributeHoldersSerializable(_attributes.getAllAttributeHolders());

        SerializableMonitor monitor = new SerializableMonitor(null);
        monitor.setAllAttributeHolders(serializableAttributes);

        return monitor;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("[").append(getClass()).append(" attributes=");
        buf.append(_attributes);
        buf.append(" level=").append(_monitoringLevel).append("]");

        return buf.toString();
    }

    protected void init(String name) {
        MonitoringEngine.getInstance().initMonitor(this);

        name = CharSetUtils.delete(name,invalidCharacters);
        set(Attribute.NAME, name);

        MonitoringEngine.getInstance().monitorCreated(this);

        _processed = false;
    }

    protected void process() {
        if (_processed) {
            log.error("This monitor has already been processed: " + this);
        } else {
            MonitoringEngine.getInstance().process(this);
            _processed = true;
        }
    }

    protected AttributeMap createAttributeMap() {
        return new AttributeMap();
    }

    protected AttributeMap getAttributes() {
        return _attributes;
    }
}
