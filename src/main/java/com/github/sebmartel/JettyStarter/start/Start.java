package com.github.sebmartel.JettyStarter.start;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.io.CharStreams;

/**
 * Jetty Start
 * 
 */
public class Start implements Runnable {
	
	Server server;
	
	public Start() {		
	}
	

	public void run() {
		server = new Server(8080);	
		server.setHandler(helloWorldHandler());
		try {
			server.start();
			server.join();
		} catch (InterruptedException e) {
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void stop() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		server.setHandler(helloWorldHandler());
		server.start();
		server.join();
	}

	private static Handler helloWorldHandler() {

		return new AbstractHandler() {

			@Override
			public void handle(String target, Request baseRequest,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {
				if (!target.startsWith("/hello"))
					return;
				
				String id = "World!";
				switch (baseRequest.getMethod()) {
				case "GET": {
					id = request.getParameter("id");
					break;
				}
				case "POST": {
					try (Reader r = new InputStreamReader(request.getInputStream())) {	// 
						id = CharStreams.toString(r);
					}
					break;
				}
				default:
				}
				response.setContentType("text/html;charset=utf-8");
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
				response.getWriter().format("<h1>Hello %s</h1>", id);
			}
		};
	}




}