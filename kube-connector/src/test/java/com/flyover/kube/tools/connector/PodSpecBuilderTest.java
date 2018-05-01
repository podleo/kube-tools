/**
 * 
 */
package com.flyover.kube.tools.connector;

import static com.flyover.kube.tools.connector.PodSpec.Builders.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author mramach
 *
 */
public class PodSpecBuilderTest {
	
	@Test
	public void testContainer() {
		
		Secret s = new Secret(null)
			.data("foo", "bar");
		
		Container c = container("foo")
			.image("bar")
			.tcpPort(80)
			.env("key", "value")
			.env("key", "foo", s);
		
		assertEquals("foo", c.model().getName());
		assertEquals("bar", c.model().getImage());
		assertEquals(1, c.model().getPorts().size());
		assertEquals(2, c.model().getEnv().size());
		
	}

}
