package hormone;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * Minimalist yet generic virtual proxy class.
 */
public class Proxy implements InvocationHandler {
    Object instance = null;
    Class iface;
    Supplier factory;

    public Proxy(Class iface, Supplier factory) {
        this.iface = iface;
        this.factory = factory;
    }

    public Object getInstance() {
        return java.lang.reflect.Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] {iface}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (instance == null) {
            instance = factory.get();
        }
        return method.invoke(instance, args);
    }
}
