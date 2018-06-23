package refried.rfid_inventory.inventoryinteractor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.uk.tsl.rfid.asciiprotocol.commands.FactoryDefaultsCommand;
import com.uk.tsl.rfid.asciiprotocol.commands.InventoryCommand;
import com.uk.tsl.rfid.asciiprotocol.enumerations.TriState;
import com.uk.tsl.rfid.asciiprotocol.responders.ICommandResponseLifecycleDelegate;
import com.uk.tsl.rfid.asciiprotocol.responders.ITransponderReceivedDelegate;
import com.uk.tsl.rfid.asciiprotocol.responders.LoggerResponder;
import com.uk.tsl.rfid.asciiprotocol.responders.TransponderData;
import com.uk.tsl.utils.HexEncoding;

import java.util.Locale;

import refried.rfid_inventory.bluetooth.TSLModel;
import refried.rfid_inventory.bluetooth.TSLModelContract;

/**
 * Interactor using the TSL InventoryCommand.
 *
 * Uses ASCII Commander as defined by TSLModel.java
 */

public class RFIDInventoryInteractor implements RFIDInventoryContract {
    public static final int MESSAGE_NOTIFICATION = 2;
    public static final int ERROR_NOTIFICATION = 3;
    private Handler mHandler = null;
    private InventoryCommand mInventoryCommand;
    private InventoryCommand mInventoryResponder;
    private LoggerResponder mLoggerResponder = new LoggerResponder();
    private itemFoundCallback mCallback;
    private TSLModelContract mModel = TSLModel.getInstance();
    private boolean mAnyTagSeen;

    public RFIDInventoryInteractor() {
        mInventoryCommand = new InventoryCommand();
        mInventoryCommand.setCaptureNonLibraryResponses(true);
        mInventoryCommand.setIncludeTransponderRssi(TriState.YES);
        mInventoryCommand.setIncludeChecksum(TriState.YES);
        mInventoryCommand.setIncludePC(TriState.YES);
        mInventoryCommand.setIncludeDateTime(TriState.YES);
        mInventoryResponder = new InventoryCommand();
        mInventoryResponder.setCaptureNonLibraryResponses(true);
        mInventoryResponder.setTransponderReceivedDelegate(new ITransponderReceivedDelegate() {
            int mTagsSeen = 0;
            @Override
            public void transponderReceived(TransponderData transponder, boolean moreAvailable) {
                mAnyTagSeen = true;
                sendMessageNotification(transponder.getEpc());
                mTagsSeen++;
                if( !moreAvailable) {
                    sendErrorNotification("Done scanning");
                    Log.d("TagCount",String.format("Tags seen: %s", mTagsSeen));
                }

            }
        });

        mInventoryResponder.setResponseLifecycleDelegate(new ICommandResponseLifecycleDelegate() {
            @Override
            public void responseBegan() {
                mAnyTagSeen = false;
            }
            @Override
            public void responseEnded() {
                if( !mAnyTagSeen && mInventoryCommand.getTakeNoAction() != TriState.YES) {
                    //sendMessageNotification("No transponders seen");

                }
                mInventoryCommand.setTakeNoAction(TriState.NO);
            }
        });
    }

    public InventoryCommand getCommand() { return mInventoryCommand; }


    @Override
    public void attachListener(itemFoundCallback c){
        mCallback = c;

        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch(msg.what) {
                    case MESSAGE_NOTIFICATION:
                        mCallback.addRFIDTagID((String) msg.obj);
                        break;
                    case ERROR_NOTIFICATION:
                        Log.d("TSL ERROR", (String) msg.obj);
                    default:
                }
               return false;
            }
        });
        mModel.getCommander().addSynchronousResponder();
        mModel.getCommander().addResponder(mInventoryResponder);
        mModel.getCommander().addResponder(mLoggerResponder);
        mModel.getCommander().addSynchronousResponder();
    }

    @Override
    public void detachListener() {
        mCallback = null;
        mHandler.removeMessages(MESSAGE_NOTIFICATION);
        mModel.getCommander().removeSynchronousResponder();
        mModel.getCommander().removeResponder(mLoggerResponder);
        mModel.getCommander().removeResponder(mInventoryResponder);
    }

    @Override
    public void resetToDefault() {
        if(mModel.getCommander().isConnected()) {
            mModel.getCommander().executeCommand(new FactoryDefaultsCommand());
        }
    }

    @Override
    public void scan() {
        if(mModel.getCommander().isConnected()) {
            mInventoryCommand.setTakeNoAction(TriState.NO);
            mModel.getCommander().executeCommand(mInventoryCommand);
        }
    }

    /**
     * Send an RFID tag message to the client using the current Handler
     *
     * @param message The message to send as String
     */
    private void sendMessageNotification(String message) {
        if( mHandler != null ) {
            Message msg = mHandler.obtainMessage(MESSAGE_NOTIFICATION, message);
            mHandler.sendMessage(msg);
        }
    }

    private void sendErrorNotification(String message) {
        if(mHandler != null) {
            Message msg = mHandler.obtainMessage(ERROR_NOTIFICATION, message);
            mHandler.sendMessage(msg);
        }
    }

    public void disconnect(){
        mModel.disconnectFrom();
    }

    public void updateConfig()
    {
        if(mModel.getCommander().isConnected()) {
            mInventoryCommand.setTakeNoAction(TriState.YES);
            mModel.getCommander().executeCommand(mInventoryCommand);
        }
    }
}
