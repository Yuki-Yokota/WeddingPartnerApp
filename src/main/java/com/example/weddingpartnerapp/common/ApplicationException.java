package com.example.weddingpartnerapp.common;
public class ApplicationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private ErrorCode errorCode;
	public ApplicationException(ErrorCode errorCode){
		super(Util.getMessage(errorCode));
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
}
