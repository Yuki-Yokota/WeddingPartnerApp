package com.example.weddingpartnerapp.common;

public enum Action {
	INACTIVE,
	REGISTER_FORM,
	UPDATE_FORM,
	DELETE_FORM,
	CSV_IMPORT_FORM,
	GET,
	REGISTER,
	UPDATE,
	DELETE,
	CSV_IMPORT,
	PDF,
	TODO_ADD,
	TODO_EDIT,
	COMPLETE,
	MAIL_DISTRIBUTION,
	MAIL_DISTRIBUTION_FORM;

	public static Action form(String actionParam) {
		try {
			if(actionParam!=null) {
				return Action.valueOf(actionParam);
			}else {
				return INACTIVE;
			}
		}catch(IllegalArgumentException e) {
			new ApplicationException(ErrorCode.SYSTEM_ERROR);
		}
		return null;
		
	}
	
	
	
	
}
