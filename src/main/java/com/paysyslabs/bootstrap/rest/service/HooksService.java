package com.paysyslabs.bootstrap.rest.service;

import com.paysyslabs.bootstrap.rest.hook.AfterParsingParametersHook;
import com.paysyslabs.bootstrap.rest.hook.AfterResponseHook;

public interface HooksService {
	AfterResponseHook getAfterResponseHook();
	AfterParsingParametersHook getAfterParsingParametersHook();
}
