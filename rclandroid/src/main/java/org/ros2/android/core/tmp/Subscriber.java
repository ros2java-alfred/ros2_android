package org.ros2.android.core.tmp;

import org.ros2.rcljava.internal.message.Message;
import org.ros2.rcljava.node.topic.Subscription;
import org.ros2.rcljava.node.topic.SubscriptionCallback;

import java.util.ArrayList;
import java.util.List;

public class Subscriber<T extends  Message>{

    /** Internal Subscription. */
    private Subscription<T> subscription;

    private final List<SubscriptionCallback<T>> callbacks = new ArrayList<SubscriptionCallback<T>>();

//    public static <U extends Message> Subscriber<U> makeSubscriber(Node node, TopicType topic) {
//        Subscriber<U> subscriber = new Subscriber<U>();
//        Subscription subscription = node.createSubscription(
//                (Class<U>) topic.getMessageType(),
//                topic.getName(),
//                subscriber.getCallback());
//        subscriber.setSubscription(subscription);
//
//        return subscriber;
//    }

    /** Global callback. */
    private final SubscriptionCallback<T> callback = new SubscriptionCallback<T>() {
        @Override
        public void dispatch(T t) {
            for (SubscriptionCallback<T> item : callbacks) {
                item.dispatch(t);
            }
        }
    };

    private Subscriber() { }

    public void addMessageListener(SubscriptionCallback<T> consumer) {
        if (!this.callbacks.contains(consumer)) {
            this.callbacks.add(consumer);
        }
    }

    private void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    private SubscriptionCallback<T> getCallback() {
        return callback;
    }
}
