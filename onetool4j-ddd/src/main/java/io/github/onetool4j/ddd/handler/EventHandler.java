package io.github.onetool4j.ddd.handler;

/**
 * 事件处理器
 *
 * @author admin
 */
public abstract class EventHandler<E> extends GenericHandler<E, Void> {

    @Override
    Void doHandle(E request) {
        onEvent(request);
        return null;
    }

    protected abstract void onEvent(E event);
}
