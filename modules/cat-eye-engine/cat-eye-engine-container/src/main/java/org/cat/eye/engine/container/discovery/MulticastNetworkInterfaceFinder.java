package org.cat.eye.engine.container.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * Created by Kotov at 12.08.2017
 */
public class MulticastNetworkInterfaceFinder {

    private Logger LOGGER = LoggerFactory.getLogger(MulticastNetworkInterfaceFinder.class);

    private List<NetworkInterface> multicastInterfaces = new ArrayList<>();

    public List<NetworkInterface> getMulticastInterfaces() throws SocketException {
        findAllMulticastInterfaces();
        return this.multicastInterfaces;
    }

    private void findAllMulticastInterfaces() throws SocketException {

        Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();

        while (interfaceEnumeration.hasMoreElements()) {
            NetworkInterface net = interfaceEnumeration.nextElement();
            if (net.supportsMulticast() && !net.isVirtual() && net.isUp() && !net.isLoopback()) {
                multicastInterfaces.add(net);
                LOGGER.info("findAllMulticastInterfaces - " +
                        "Multicast interface {} was found.", net.getName());
            }
        }
    }
}
