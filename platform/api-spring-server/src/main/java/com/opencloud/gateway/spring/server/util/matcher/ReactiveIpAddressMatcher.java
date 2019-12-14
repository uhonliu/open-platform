package com.opencloud.gateway.spring.server.util.matcher;

import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * IP匹配工具类
 */
public final class ReactiveIpAddressMatcher {
    private final int nMaskBits;
    private final InetAddress requiredAddress;

    public ReactiveIpAddressMatcher(String ipAddress) {
        if (ipAddress.indexOf(47) > 0) {
            String[] addressAndMask = StringUtils.split(ipAddress, "/");
            ipAddress = addressAndMask[0];
            this.nMaskBits = Integer.parseInt(addressAndMask[1]);
        } else {
            this.nMaskBits = -1;
        }

        this.requiredAddress = this.parseAddress(ipAddress);
    }

    public boolean matches(String address) {
        InetAddress remoteAddress = this.parseAddress(address);
        if (!this.requiredAddress.getClass().equals(remoteAddress.getClass())) {
            return false;
        } else if (this.nMaskBits < 0) {
            return remoteAddress.equals(this.requiredAddress);
        } else {
            byte[] remAddr = remoteAddress.getAddress();
            byte[] reqAddr = this.requiredAddress.getAddress();
            int oddBits = this.nMaskBits % 8;
            int nMaskBytes = this.nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
            byte[] mask = new byte[nMaskBytes];
            Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) -1);
            int i;
            if (oddBits != 0) {
                i = (1 << oddBits) - 1;
                i <<= 8 - oddBits;
                mask[mask.length - 1] = (byte) i;
            }

            for (i = 0; i < mask.length; ++i) {
                if ((remAddr[i] & mask[i]) != (reqAddr[i] & mask[i])) {
                    return false;
                }
            }

            return true;
        }
    }

    private InetAddress parseAddress(String address) {
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException var3) {
            throw new IllegalArgumentException("Failed to parse address" + address, var3);
        }
    }
}
