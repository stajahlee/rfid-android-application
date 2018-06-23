//----------------------------------------------------------------------------------------------
// Copyright (c) 2013 Technology Solutions UK Ltd. All rights reserved.
//----------------------------------------------------------------------------------------------

package com.uk.tsl.rfid;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.uk.tsl.rfid.asciiprotocol.AsciiCommander;
import com.uk.tsl.rfid.devicelist.BuildConfig;

import java.util.Date;

public class ModelBase {

	// Debugging
	private static final boolean D = BuildConfig.DEBUG;

	// Model busy state changed message
	public static final int BUSY_STATE_CHANGED_NOTIFICATION = 1;
	public static final int MESSAGE_NOTIFICATION = 2;

	// 
	protected Handler mHandler;
	protected boolean mBusy;
	private Exception mException;
	protected AsciiCommander mCommander;
	protected AsyncTask<Void, Void, Void> mTaskRunner;
	protected double mLastTaskExecutionDuration;

	private Date mTaskStartTime;

	/**
	 * @return true if the model is currently performing a task
	 */
	public boolean isBusy() { return mBusy; }

	/**
	 * Set the task busy state
	 * @param isBusy
	 */
	protected void setBusy(boolean isBusy)
	{
		if( mBusy != isBusy  )
		{
			mBusy = isBusy;

			if( mHandler != null )
			{
				Message msg = mHandler.obtainMessage(BUSY_STATE_CHANGED_NOTIFICATION, isBusy);
	        	mHandler.sendMessage(msg);
			}
		}
	}


	/**
	 * Send a message to the client using the current Handler
	 * 
	 * @param message The message to send as String
	 */
	protected void sendMessageNotification(String message)
	{
		if( mHandler != null )
		{
			Message msg = mHandler.obtainMessage(MESSAGE_NOTIFICATION, message);
			mHandler.sendMessage(msg);
		}
	}

	public boolean isTaskRunning() { return mTaskRunner != null; }
	
	public ModelBase()
	{
		mCommander = null;
		mHandler = null;
		mBusy = false;
		mLastTaskExecutionDuration = -1.00;
	}

	/**
	 * @return the commander the model uses
	 */
	public AsciiCommander getCommander() { return mCommander; }
	/**
	 * @param commander the commander the model uses
	 */
	public void setCommander(AsciiCommander commander) { mCommander = commander; }

	/**
	 * @return the handler for model notifications
	 */
	public Handler getHandler() { return mHandler; }
	/**
	 * @param handler the handler for model notifications
	 */
	public void setHandler(Handler handler) { mHandler = handler; }

	
	/**
	 * @return the error as an exception or null if none
	 */
	public Exception error() { return mException; }

	/**
	 * @param e the error as an exception
	 */
	protected void setError(Exception e) { mException = e; }

	/**
	 * @return the current execution duration if a task is running otherwise the duration of the last task
	 */
	public final double getTaskExecutionDuration() {
		if( mLastTaskExecutionDuration >= 0.0 ) {
			return mLastTaskExecutionDuration;
		} else {
			Date now = new Date();
			return (now.getTime() - mTaskStartTime.getTime()) / 1000.0;
		}
	}

	@TargetApi(11)
	/**
	 * Execute the given task
	 * 
	 * The busy state is notified to the client
	 * 
	 * Tasks should throw an exception to indicate (and return) error
	 * 
	 * @param task the Runnable task to be performed 
	 */
	public void performTask(Runnable task) throws ModelException
	{
		final Runnable rTask = task;

		if( mCommander == null ) {
			throw( new ModelException("There is no AsciiCommander set for this model!") );
		} else {
			if( mTaskRunner != null ) {
				throw( new ModelException("Task is already running!"));
			} else {
				mTaskRunner = new AsyncTask<Void, Void, Void>() {
					@Override
					protected void onPreExecute()
					{
						mLastTaskExecutionDuration = -1.0;
						mTaskStartTime = new Date();
					}

					protected Void doInBackground(Void... voids)
					{
						try {
							setBusy(true);
							mException = null;

							rTask.run();

						} catch (Exception e) {
							mException = e;
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						mTaskRunner = null;
						setBusy(false);			

						// Update the time taken
						Date finishTime = new Date();
						mLastTaskExecutionDuration = (finishTime.getTime() - mTaskStartTime.getTime()) / 1000.0;

						if(D) Log.i(getClass().getName(), String.format("Time taken (ms): %d %.2f", finishTime.getTime() - mTaskStartTime.getTime(), mLastTaskExecutionDuration));
					}
				};

				try {
					if( android.os.Build.VERSION.SDK_INT >= 11) {
						// Ensure the tasks are executed serially whatever newer API versions may choose by default
						mTaskRunner.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Void[])null);
					} else {
						// Tasks will be executed concurrently on API < 11
						mTaskRunner.execute((Void[])null);
					}
				} catch( Exception e ) {
					mException = e;
				}
			}
		}
	}

}
