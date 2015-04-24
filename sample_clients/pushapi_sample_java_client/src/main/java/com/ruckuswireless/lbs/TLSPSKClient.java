package com.ruckuswireless.lbs;

import org.bouncycastle.crypto.tls.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by yeongsheng on 14/4/15.
 */
public class TLSPSKClient extends PSKTlsClient {

    /**
     * Authentication instance to retrieve server certificate.
     *
     */
    private final TlsAuthentication	authentication	= new BCTlsAuthentication();

    /**
     * Hostname to be used in server name indication extension.
     */
    private final String			hostname;

    /**
     * Enabled cipher suites.
     */
    private static final int[]		defaultCS		= new int[] {
            0x008C, 0x008D, 0x00AE, 0x00AF          // TLS_PSK_*
            //0x008C, 0x008D, 0x00AE, 0x00AF,       // TLS_PSK_*
            //0x0094, 0x0095, 0x00B6, 0x00B7	    // TLS_RSA_PSK
    };

    /*
     * @Override(non-Javadoc)
     *
     * @see org.bouncycastle.crypto.tls.PSKTlsClient#getCipherSuites()
     */
    @Override
    public int[] getCipherSuites() {
        return defaultCS;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.bouncycastle.crypto.tls.AbstractTlsPeer#notifyAlertRaised(short,
     * short, java.lang.String, java.lang.Exception)
     */
    @Override
    public void notifyAlertRaised(short alertLevel, short alertDescription, String message, Throwable cause){
        System.out.println("### ALERT RAISED: " + alertLevel + " / " + alertDescription + " = " + message + ", " + cause);
    }

    @Override
    public void notifySecureRenegotiation(final boolean secureRenegotiation) throws IOException {
        //System.out.println("### RENEGO: secure? " + secureRenegotiation);
        //throw new TlsFatalAlert((short) 100);
    }

    /**
     * Create an instance of TLSPSKClient
     *
     * @param hostname
     *            - optional server name for server name indication, SNI will be
     *            omitted if <em>null</em>
     * @param pskId
     *            - pre-shared key username / eID session id
     * @param pskSecret
     *            - corresponding secret for eID session
     */
    public TLSPSKClient(final String hostname, final byte[] pskId, final byte[] pskSecret) {
        super(new TlsPSKIdentity() {

            /*
             * (non-Javadoc)
             *
             * @see
             * org.bouncycastle.crypto.tls.TlsPSKIdentity#skipIdentityHint()
             */
            @Override
            public void skipIdentityHint() {
            }

            /*
             * (non-Javadoc)
             *
             * @see
             * org.bouncycastle.crypto.tls.TlsPSKIdentity#notifyIdentityHint
             * (byte[])
             */
            @Override
            public void notifyIdentityHint(final byte[] psk_identity_hint) {
                System.out.println("PSK id hint: " + new String(psk_identity_hint));
            }

            /*
             * @Override(non-Javadoc)
             *
             * @see org.bouncycastle.crypto.tls.TlsPSKIdentity#getPSKIdentity()
             */
            @Override
            public byte[] getPSKIdentity() {
                return pskId;
            }

            /*
             * (non-Javadoc)
             *
             * @see org.bouncycastle.crypto.tls.TlsPSKIdentity#getPSK()
             */
            @Override
            public byte[] getPSK() {
                return pskSecret;
            }
        });
        this.hostname = hostname;
    }

    /**
     * Create an instance of TLSPSKClient with no server name indication.
     *
     * @param pskId
     *            - pre-shared key username / eID session id
     * @param pskSecret
     *            - corresponding secret for eID session
     */
    public TLSPSKClient(final byte[] pskId, final byte[] pskSecret) {
        this(null, pskId, pskSecret);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.bouncycastle.crypto.tls.TlsClient#getAuthentication()
     */
    @Override
    public TlsAuthentication getAuthentication() throws IOException {
        return this.authentication;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.bouncycastle.crypto.tls.AbstractTlsClient#getMinimumVersion()
     */
    @Override
    public ProtocolVersion getMinimumVersion() {
        return ProtocolVersion.TLSv10;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.bouncycastle.crypto.tls.AbstractTlsClient#getClientExtensions()
     */
    @Override
    public Hashtable<Integer, byte[]> getClientExtensions() throws IOException {
        @SuppressWarnings("unchecked")
        Hashtable<Integer, byte[]> clientExtensions = super.getClientExtensions();
        if (clientExtensions == null) {
            clientExtensions = new Hashtable<Integer, byte[]>();
        }

        final ByteArrayOutputStream byteArrOPStream = new ByteArrayOutputStream();
        final DataOutputStream dataOPStream = new DataOutputStream(byteArrOPStream);

        if (this.hostname != null) {
            final byte[] hostnameBytes = this.hostname.getBytes();
            final int snl = hostnameBytes.length;

            // OpenSSL breaks if an extension with length "0" sent, they expect at least
            // an entry with length "0"
            dataOPStream.writeShort(snl == 0 ? 0 : snl + 3); // entry size
            if (snl > 0) {
                dataOPStream.writeByte(0); // name type = hostname
                dataOPStream.writeShort(snl); // name size
                if (snl > 0) {
                    dataOPStream.write(hostnameBytes);
                }
            }

            dataOPStream.close();
            clientExtensions.put(ExtensionType.server_name, byteArrOPStream.toByteArray());
        }

        return clientExtensions;
    }

}

