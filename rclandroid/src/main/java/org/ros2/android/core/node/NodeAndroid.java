package org.ros2.android.core.node;

import android.content.Context;
import android.util.Log;

import org.ros2.rcljava.node.Node;

import java.net.UnknownHostException;

//import com.rosalfred.android.core.AlfredApplicationBase;
//import com.rosalfred.android.core.entity.RosMaster;
//import com.rosalfred.android.core.entity.RosNode;
//import com.rosalfred.android.core.entity.RosNodeType;
//import com.rosalfred.android.core.provider.utils.RosMasterProviderUtils;
//import com.rosalfred.android.core.ros.node.base.TopicDirection;
//import com.rosalfred.android.core.ros.node.base.TopicType;
//import com.rosalfred.android.core.ros.remotenode.RemoteNodeLanguage;
//import com.rosalfred.android.core.ros.remotenode.base.RemoteNodeBase;
//
//import org.ros.android.AbstractNodeMain;
//import org.ros.android.Subscriber;
//import org.ros2.rcljava.internal.message.Message;
//import org.ros2.rcljava.internal.service.Service;
//import org.ros2.rcljava.node.service.Client;
//import org.ros2.rcljava.node.service.NativeClient;
//import org.ros2.rcljava.node.topic.NativePublisher;
//import org.ros2.rcljava.node.topic.Publisher;
//import org.ros2.rcljava.node.topic.SubscriptionCallback;

/**
 * Main node of Alfred Android.
 */
public final class NodeAndroid implements NodeManager {

	private static final String TAG = "NodeAndroid";

	private final Context context;

	// Ros Node implementation
	private Node node;
//	private ArrayList<RemoteNodeBase> remoteNodes = new ArrayList<RemoteNodeBase>();
////	private ArrayList<NodeAbstract> localSubNode = new ArrayList<NodeAbstract>();
//	private static volatile RemoteNodeBase currentRemoteNode = null;
//
//    public Node getRosNode() {
//        return this.node;
//    }
//
//	// Topics management
//	private HashMap<String, Publisher<Message>> publishers = new HashMap<String, Publisher<Message>>();
//	private HashMap<String, Subscriber<Message>> subscribers = new HashMap<String, Subscriber<Message>>();
//	private HashMap<String, Client<Service>> services = new HashMap<String, Client<Service>>();
//
//	// Callback on NodeAndroid loaded
//	private List<NodeAndroidListener> listener
//	       = new ArrayList<NodeAndroidListener>();
//
	public NodeAndroid(Context context) {
		this.context = context;
	}
//
//	public void load() {
//	    Log.d(TAG, "Load Node Android");
//
////	    this.rosMaster = new RosMasterProviderUtils(this.context).getCurrent();
//
//	    this.remoteNodes.clear();
//	    this.publishers.clear();
//	    this.subscribers.clear();
//	    this.services.clear();
//
//	    if (this.rosMaster != null) {
//            this.loadRemoteNode();
//            this.loadDefaultRemoteNode();
//	    }
//
//        // Select current Remote Node
//        if (this.remoteNodes.size() > 0) {
//            NodeAndroid.currentRemoteNode = this.remoteNodes.get(0);
//        }
//
//        //Call loaded listeners
//        for (NodeAndroidListener listener : this.listener) {
//            listener.onNodeAndroidLoaded();
//        }
//	}
//
//	public URI getMasterUri() {
//		return this.masterUri;
//	}
//
//	public Context getContext() {
//	    return this.context;
//	}
//
//	/**
//	 * Disconnect the Android Service of the ROS component.
//	 */
//	public void disconnect(Context ctx) {
//		if (this.node != null) {
//
//		}
//	}
//
//	// Topics Process
//	/**
//	 * Detect all Subscribers of Remote Node ROS and listen then.
//	 */
//	private void listeningTopics() {
//		if (this.node != null) {
//			Subscriber<Message> subscriber;
//
//			for (RemoteNodeBase remoteNode : this.remoteNodes) {
//				ArrayList<TopicType> topics = remoteNode.getWatchedTopics(TopicDirection.IN);
//
//				for (TopicType topic : topics) {
//					subscriber = null;
//
//					if (!this.subscribers.containsKey(topic.getName()) ) {
//						Log.d(TAG, "Subscriber topic : " + topic.getName() + "..." );
//
//						subscriber = Subscriber.makeSubscriber(this.node, topic);
//
//						if (subscriber != null) {
//							Log.d(TAG, "Subscriber topic  : " + topic.getName() + " done." );
//							this.subscribers.put(topic.getName() ,subscriber);
//						}
//					} else {
//						subscriber = this.subscribers.get(topic.getName());
//						Log.d(TAG, "Existing subscriber topic : " + topic.getName() + "." );
//
//						// Reconnect
//					}
//				}
//
//			}
//		}
//	}
//
//	/**
//	 * Detect all Publisher of Remote Node ROS and register then.
//	 */
//	private void publishingTopics() {
//		if (this.node != null) {
//			Publisher<Message> publisher;
//
//			for (RemoteNodeBase remoteNode : this.remoteNodes) {
//				ArrayList<TopicType> topics = remoteNode.getWatchedTopics(TopicDirection.OUT);
//
//				for (TopicType topic : topics) {
//					publisher = null;
//
//					if (!this.publishers.containsKey(topic.getName()) ) {
//						Log.d(TAG, "Register topic for publish : " + topic.getName() + "..." );
//
//						publisher = this.node.createPublisher(
//								(Class<Message>)topic.getMessageType(),
//								topic.getName());
//
//						if (publisher != null) {
//							Log.d(TAG, "Register topic for publish : " + topic.getName() + " done." );
//
//							this.publishers.put(topic.getName() ,publisher);
//						}
//					} else {
//						publisher = this.publishers.get(topic.getName());
//
//						Log.d(TAG, "Existing publisher topic : " + topic.getName() + "." );
//
//						// Reconnect
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * Detect all Services of Remote Node ROS add them.
//	 */
//	private void serviceTopics() {
//		if (this.node != null) {
//			Client<Service> service;
//
//			for (RemoteNodeBase remoteNode : this.remoteNodes) {
//				ArrayList<TopicType> topics = remoteNode.getWatchedTopics(TopicDirection.SERVICE);
//
//				for (TopicType topic : topics) {
//					service = null;
//
//					if (!this.services.containsKey(topic.getName()) ) {
//						Log.d(TAG, "Service topic : " + topic.getName() + "..." );
//
//// TODO <---------------------------------------------------------------------
////						try {
////							service = this.node.createClient(
////									(Service)topic.getMessageType(),
////									topic.getName());
////						} catch (Exception e) {
////							e.printStackTrace();
////						}
//
//						if (service != null) {
//							Log.d(TAG, "Service topic  : " + topic.getName() + " done." );
//							this.services.put(topic.getName() , service);
//						}
//					} else {
//						service = this.services.get(topic.getName());
//
//						Log.d(TAG, "Existing subscriber topic : " + topic.getName() + "." );
//
//						// Reconnect
//					}
//				}
//
//			}
//		}
//	}

	// Call Back of ROS Node
	/**
	 * CallBack when ROS Node Master is connected.
	 */
	@Override
	public void onStart(Node node) {
        Log.d(TAG, "Connected to Ros Master");

		this.node = node;

//		this.publishingTopics();
//		this.listeningTopics();
//		this.serviceTopics();
//
//		for (RemoteNodeBase remoteNodeBase : this.remoteNodes) {
//			remoteNodeBase.onConnected();
//		}
//
//		//Call connected listeners
//        for (NodeAndroidListener listener : this.listener) {
//            listener.onNodeAndroidConnected();
//        }
	}

	/**
	 * CallBack when a ROS Node raise a Exception.
	 */
	@Override
	public void onError(Node node, Throwable throwable) {
		Log.e(TAG, "Error on ROS Node");

		if (throwable != null) {
			if (throwable.getCause().getClass().equals(UnknownHostException.class)){

				//Toast.makeText(AlfredApplication.getApplication(), "Host unknow, verify your config !", Toast.LENGTH_LONG).show();
			}

		}

	}

	/**
	 * CallBack when a ROS Node start to shutdown.
	 */
	@Override
	public void onShutdown(Node node) {
	    //super.onShutdown(node);

		this.node = null;
	}

	/**
	 * CallBack when a ROS Node finish to shutdown.
	 */
	@Override
	public void onShutdownComplete(Node node) {
		// TODO Auto-generated method stub

	}

	/**
	 * Get ROS Node name.
	 */
	@Override
	public String getDefaultNodeName() {
		return "android_" + ""; //AlfredApplicationBase.getUDID();
	}

//	// Accessors
//	@SuppressWarnings("unchecked")
//    public <T extends RemoteNodeBase> T getRemoteNode(Class<T> cls) {
//		return (T) this.getRemoteNode(cls.getName());
//
//	}
//
//	private RemoteNodeBase getRemoteNode(String name) {
//	    RemoteNodeBase result = null;
//
//		for (RemoteNodeBase remoteNode : this.remoteNodes) {
//			if (remoteNode.getClass().getName().equals(name) ) {
//				result = remoteNode;
//			}
//		}
//
//		return result;
//	}
//
//	public String[] getAvailableRemoteNodes() {
//		String[] result = new String[this.remoteNodes.size()];
//
//		for (int i = 0; i < this.remoteNodes.size(); i++) {
//			RemoteNodeBase remoteNode = this.remoteNodes.get(i);
//
//			result[i] = remoteNode.getNodeData().getPath();
//		}
//
//		return result;
//	}
//
//	public ArrayList<RemoteNodeBase> getRemoteNodes() {
//	    return this.remoteNodes;
//	}
//
//	/**
//	 * Get current ROS Node remote activate GUI
//	 * @return current ROS Node remote
//	 */
//	public RemoteNodeBase getCurrentRemoteNode() {
//		return NodeAndroid.currentRemoteNode;
//	}
//
//	/**
//	 *
//	 * @return
//	 */
//	public RosMaster getRosMaster() {
//		return this.rosMaster;
//	}
//
//	/**
//	 *
//	 * @param topicName
//	 * @param message
//	 */
//	public void publish(String topicName, Message message) {
//		if (this.node != null) {
//			Publisher<Message> publisher = this.publishers.get(topicName);
//
//			if (publisher != null) {
//				publisher.publish(message);
//			}
//		}
//	}
//
//	/**
//	 *
//	 * @param topicName
//	 * @param listener
//	 */
//	public void listen(String topicName, SubscriptionCallback<Message> listener) {
//		if (this.node != null) {
//			Subscriber<Message> subscriber = this.subscribers.get(topicName);
//
//			if (subscriber != null) {
//				subscriber.addMessageListener(listener);
//			}
//		}
//	}
//
//// TODO <------------------------------------------------------------
////	/**
////	 *
////	 * @param topicName
////	 * @param message
////	 * @param listener
////	 */
////	public void getMessage(String topicName,
////			Message message,
////			ServiceResponseListener<Service> listener) {
////		if (this.node != null) {
////			Client<Service> service = this.services.get(topicName);
////
////			if (service != null) {
////				service.call(message, listener);
////			}
////		}
////	}
//
//	/**
//	 * Set current ROS Node remote activate GUI
//	 * @param remoteNode
//	 */
//	public void setCurrentRemoteNode(RemoteNodeBase remoteNode) {
//		if (this.remoteNodes.contains(remoteNode)) {
//			NodeAndroid.currentRemoteNode = remoteNode;
//		}
//	}
//
//	/**
//	 * Load Default ROS Node remote of app
//	 */
//	private void loadDefaultRemoteNode() {
//		RemoteNodeBase result;
//
//		RosNode data = new RosNode();
//		data.setId(-1);
//		data.setPath("lang");
//		data.setType(RosNodeType.IA);
//
//		result = new RemoteNodeLanguage(
//				this.context,
//				this,
//				data);
//
//		this.remoteNodes.add(result);
//	}
//
//	/**
//	 * Load available ROS Node remote of the current Master configuration
//	 */
//	private void loadRemoteNode() {
//		RemoteNodeBase result;
//
//		if (rosMaster != null) {
//			for (RosNode rosNode : this.rosMaster.getNodes()) {
//				result = null;
//				try {
//					result = rosNode.getType().getRemoteNode().getConstructor(
//							Context.class,
//							NodeAndroid.class,
//							RosNode.class)
//								.newInstance(
//										this.context,
//										this,
//										rosNode);
//
//					// FIXME Hack !!!
//	//						if (result instanceof RemoteNodeXbmc) {
//	//							this.xbmcRos.remoteNode = (RemoteNodeXbmc) result;
//	//						}
//
//				} catch (Exception e) {
//					Log.e(TAG, e.getMessage());
//				}
//
//				if (result != null) {
//					this.remoteNodes.add(result);
//				}
//			}
//		}
//	}
//
//	/**
//	 * Factory of Message of specific publisher.
//	 * @param topicName
//	 * @return
//	 */
//	public Message makeMessage(String topicName) {
//		Message result = null;
//
//		Publisher<? extends Message> publisher = this.publishers.get(topicName);
//		if (publisher != null) {
//			try {
//				result = ((NativePublisher<?>)publisher).getMsgType().newInstance();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return result;
//	}
//
//	public Service makeService(String topicName) {
//		Service result = null;
//		if (result == null) {
//			Client<Service> service = this.services.get(topicName);
//			if (service != null) {
//				try {
//					result = ((NativeClient<?>)service).getServiceType().newInstance();
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Add a new listener for {@link NodeAndroid}.
//	 * @param listener
//	 */
//	public void addListener(NodeAndroidListener listener) {
//	    if (!this.listener.contains(listener)) {
//	        this.listener.add(listener);
//	    }
//	}
//
//	/**
//	 * Remove listener.
//	 * @param listener
//	 */
//	public void removeListener(NodeAndroidListener listener) {
//        if (this.listener.contains(listener)) {
//            this.listener.add(listener);
//        }
//    }
//
//	public interface NodeAndroidListener {
//	    /**
//	     * Call when {@link NodeAndroid} was loaded.
//	     */
//	    void onNodeAndroidLoaded();
//	    /**
//	     * Call when {@link NodeAndroid} was connected to Ros.
//	     */
//	    void onNodeAndroidConnected();
//	}
}
