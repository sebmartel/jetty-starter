package com.github.sebmartel.JettyStarter.start;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntegrationTests {
	
	public static Start app;
	public static Thread appThread; 
	public static HttpClient client;
	private static final String APP = "http://localhost:8080";
	@BeforeClass
	public static void init() throws Exception {
		app = new Start();
		appThread = new Thread( () -> app.run() );
		appThread.start();
		client = new HttpClient();
		client.start();
		
		// wait until server is up
		for (int retries = 20; retries > 0; --retries) {
			try {
				client.GET( APP + "/");
			} catch (ExecutionException e) {
				Thread.sleep(100);
				continue;
			}
			break;
		}
	}

	@AfterClass
	public static void shutdown() throws Exception {
		app.stop();
		appThread.join();		
	}

	@Before
	public void setUp() throws Exception {
	}	
	
	@Test
	public void testPostAndGet() throws InterruptedException, TimeoutException, ExecutionException {
		String ev = "Beautiful World!";
		ContentResponse res = client.POST("http://localhost:8080/hello").content(new StringContentProvider(ev)).send();
		int status = res.getStatus();
		assertThat(status, is(200));
		assertThat(res.getContentAsString(), is("<h1>hello " + ev + "</h1>"));

		res = client.GET("http://localhost:8080/hello?id=world");
		
		assertThat(res.getContentAsString(), is("<h1>hello " + ev + "</h1>"));
		assertThat(res.getStatus(), is(200));
	}
}
