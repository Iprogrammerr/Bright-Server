package com.iprogrammerr.bright.server.respondent;

import com.iprogrammerr.bright.server.binary.type.TypedBinary;
import com.iprogrammerr.bright.server.response.Response;

public interface FileRespondent {
	Response response(TypedBinary file) throws Exception;
}
