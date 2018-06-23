//----------------------------------------------------------------------------------------------
// Copyright (c) 2013 Technology Solutions UK Ltd. All rights reserved.
//----------------------------------------------------------------------------------------------

package com.uk.tsl.rfid;


import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


/**
 * A handler implementation that stores a weak reference to an object and only calls
 * {@link #handleMessage(Message, T)} on subclasses when the reference to the object
 * is not lost yet.
 * <p>
 * Basically this enables us to create static handler classes while still be able to
 * call through to the activity or context and store a reference to the handler as a
 * normal class member.
 * <p>
 * Example for activity <code>MyActivity</code>:
 * <pre>
 * private static class MyHandler extends WeakHandler&lt;MyActivity&gt; {
 *   public MyHandler(MyActivity t) {
 *     super(t);
 *   }
 *  
 *   &#064;Override
 *   public handleMessage(Message msg, MyActivity t) {
 *     // handle message normally here...
 *   }
 *  
 * }
 *
 * </pre>
 * 
 * Based on code taken from here
 * http://pastebin.com/vCLY57Ug#
 * 
 */
public abstract class WeakHandler<T> extends Handler {
 
  private final WeakReference<T> weakRef;

  /**
   * Construct a Handler that  has a weak reference to the object t of type T
   * 
   * @param t the object which this Handler will retain a reference to
   */
  public WeakHandler(T t) {
    super();
    weakRef = new WeakReference<T>(t);
  }
 
  /**
   * If the reference is valid, it invokes {@link #handleMessage(Message, T)}.
   */
  @Override
  public final void handleMessage(Message msg) {
    final T strongRef = weakRef.get();
    if (strongRef != null) {
      handleMessage(msg, strongRef);
    }
  }
 
  /**
   * Must be implemented by subclasses in order to handle messages.
   *
   * @param msg The message.
   * @param t A strong reference to the object (usually an activity) which is guaranteed not to be <code>null</code>.
   * @see Handler#handleMessage(Message)
   */
  public abstract void handleMessage(Message msg, T t);
 
}