package com.iprogrammerr.bright.server.response.template;

import java.util.List;

import com.iprogrammerr.bright.server.header.Header;
import com.iprogrammerr.bright.server.response.ContentResponse;
import com.iprogrammerr.bright.server.response.EmptyResponse;
import com.iprogrammerr.bright.server.response.ResponseEnvelope;
import com.iprogrammerr.bright.server.response.body.ResponseBody;

public final class UnauthenticatedResponse extends ResponseEnvelope {

	private static final int CODE = 401;

	public UnauthenticatedResponse(Header... headers) {
		super(new EmptyResponse(CODE, headers));
	}

	public UnauthenticatedResponse(String message, Header... headers) {
		super(new ContentResponse(CODE, message, headers));
	}

	public UnauthenticatedResponse(String message, List<Header> headers) {
		super(new ContentResponse(CODE, message, headers));
	}

	public UnauthenticatedResponse(ResponseBody body, Header... headers) {
		super(new ContentResponse(CODE, body, headers));
	}

	public UnauthenticatedResponse(ResponseBody body, List<Header> headers) {
		super(new ContentResponse(CODE, body, headers));
	}
}
