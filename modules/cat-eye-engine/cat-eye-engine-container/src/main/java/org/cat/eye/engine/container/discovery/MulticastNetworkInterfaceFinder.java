package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MulticastNetworkInterfaceFinder {

    private Logger LOGGER = LoggerFactory.getLogger(MulticastNetworkInterfaceFinder.class);

    public void getAllMulticastInterfaces() throws SocketException {

        Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();

        while (interfaceEnumeration.hasMoreElements()) {
            NetworkInterface net = interfaceEnumeration.nextElement();
            net.supportsMulticast();
        }
    }
}
