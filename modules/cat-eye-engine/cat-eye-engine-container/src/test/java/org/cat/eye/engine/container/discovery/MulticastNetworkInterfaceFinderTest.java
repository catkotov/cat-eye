package org.cat.eye.engine.container.discovery;

import org.junit.Test;
import java.net.NetworkInterface;
import java.util.List;

import static org.junit.Assert.*;

public class MulticastNetworkInterfaceFinderTest {

    @Test
    public void getAllMulticastInterfaces() throws Exception {
        MulticastNetworkInterfaceFinder finder = new MulticastNetworkInterfaceFinder();
        List<NetworkInterface> interfaces = finder.getMulticastInterfaces();
        assertNotNull(interfaces);
    }

}