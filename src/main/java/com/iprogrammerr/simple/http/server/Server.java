package com.iprogrammerr.simple.http.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.iprogrammerr.simple.http.server.configuration.ServerConfiguration;
import com.iprogrammerr.simple.http.server.constants.RequestMethod;
import com.iprogrammerr.simple.http.server.constants.ResponseCode;
import com.iprogrammerr.simple.http.server.exception.InitializationException;
import com.iprogrammerr.simple.http.server.exception.ObjectNotFoundException;
import com.iprogrammerr.simple.http.server.filter.RequestFilter;
import com.iprogrammerr.simple.http.server.model.Request;
import com.iprogrammerr.simple.http.server.model.Response;
import com.iprogrammerr.simple.http.server.parser.RequestParser;
import com.iprogrammerr.simple.http.server.parser.ResponseParser;
import com.iprogrammerr.simple.http.server.parser.UrlParser;
import com.iprogrammerr.simple.http.server.resolver.RequestResolver;

public class Server {

    private ServerSocket serverSocket;
    private Executor executor = Executors.newCachedThreadPool();
    private RequestParser requestParser;
    private ResponseParser responseParser;
    private boolean stopped;
    private String contextPath;
    private List<RequestResolver> requestResolvers;
    private List<RequestFilter> requestFilters;

    public Server(ServerConfiguration serverConfiguration, List<RequestResolver> requestsResolvers,
	    List<RequestFilter> requestFilters) {
	try {
	    this.serverSocket = new ServerSocket(serverConfiguration.getPort());
	} catch (IOException exception) {
	    throw new InitializationException(exception);
	}
	this.contextPath = serverConfiguration.getContextPath();
	this.requestParser = new RequestParser(UrlParser.getInstance());
	this.responseParser = new ResponseParser(serverConfiguration);
	this.requestResolvers = requestsResolvers;
	this.requestFilters = requestFilters;
    }

    public Server(ServerConfiguration serverConfiguration, List<RequestResolver> requestsResolvers) {
	this(serverConfiguration, requestsResolvers, new ArrayList<>());
    }

    public Server(ServerConfiguration serverConfiguration) {
	this(serverConfiguration, new ArrayList<>(), new ArrayList<>());
    }

    public void start() {
	while (!isStopped()) {
	    try {
		System.out.println("Waiting for connection...");
		Socket socket = serverSocket.accept();
		executor.execute(getRequestHandler(socket));
	    } catch (IOException exception) {
		exception.printStackTrace();
	    }
	}
    }

    private Runnable getRequestHandler(Socket socket) {
	return () -> {
	    try (InputStream inputStream = socket.getInputStream();
		    BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream())) {
		Request request = requestParser.getRequest(inputStream);
		System.out.println(request);
		Response response = resolve(request);
		System.out.println("Writing..." + response);
		byte[] rawResponse = responseParser.getResponse(response);
		outputStream.write(rawResponse);
	    } catch (IOException exception) {
		exception.printStackTrace();
	    } finally {
		try {
		    socket.close();
		} catch (IOException exception) {
		    exception.printStackTrace();
		}
	    }
	};
    }

    public Response resolve(Request request) {
	System.out.println("RequestResolver.resolve()");
	Response response = new Response();
	if (!request.getPath().startsWith(contextPath)) {
	    return response;
	}
	try {
	    RequestMethod requestMethod = RequestMethod.createFromString(request.getMethod());
	    // TODO proper JS's OPTIONS Handling
	    if (requestMethod.equals(RequestMethod.OPTIONS)) {
		response.setCode(ResponseCode.OK);
		return response;
	    }
	    System.out.println(requestMethod);
	    request.removeContextFromPath(contextPath);
	    RequestResolver resolver = getResolver(requestMethod, request);
	    if (filter(requestMethod, request, response)) {
		resolver.handle(request, response);
	    }
	} catch (ObjectNotFoundException exception) {
	    exception.printStackTrace();
	}
	return response;
    }

    private RequestResolver getResolver(RequestMethod requestMethod, Request request) {
	for (RequestResolver resolver : requestResolvers) {
	    if (resolver.canHandle(requestMethod, request)) {
		return resolver;
	    }
	}
	throw new ObjectNotFoundException();
    }

    private boolean filter(RequestMethod requestMethod, Request request, Response response) {
	for (RequestFilter filter : requestFilters) {
	    if (filter.shouldFilter(request.getPath(), requestMethod)) {
		return filter.isValid(request, response);
	    }
	}
	return true;
    }

    public String getContextPath() {
	return contextPath;
    }

    private synchronized boolean isStopped() {
	return stopped;
    }

    public void stop() {
	stopped = true;
	try {
	    serverSocket.close();
	} catch (IOException exception) {
	    exception.printStackTrace();
	}
    }
}
