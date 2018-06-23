//----------------------------------------------------------------------------------------------
// Copyright (c) 2013 Technology Solutions UK Ltd. All rights reserved.
//----------------------------------------------------------------------------------------------

package com.uk.tsl.rfid;

public class ModelException extends Exception {

	private static final long serialVersionUID = 469698280001919043L;

	public ModelException() {
	}

	public ModelException(String detailMessage) {
		super(detailMessage);
	}

	public ModelException(Throwable throwable) {
		super(throwable);
	}

	public ModelException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
