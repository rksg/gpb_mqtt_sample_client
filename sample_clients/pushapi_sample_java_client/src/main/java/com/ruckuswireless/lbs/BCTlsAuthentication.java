package com.ruckuswireless.lbs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsCredentials;

/**
 * Created by yeongsheng on 14/4/15.
 */
public class BCTlsAuthentication implements TlsAuthentication {

    /**
     * The List of X509 - server certificates.
     */
    List<Certificate>	serverCertList;

    /**
     * Converts every server certificate in an X509-certificate and stores it
     * globally.
     *
     * @param serverCertificate
     *            - The server certificate, which related server certificate
     *            list needs to be notified.
     * @throws IOException
     *             If an error occurs during the certificate retrieval.
     */
    @Override
    public void notifyServerCertificate(final org.bouncycastle.crypto.tls.Certificate serverCertificate) throws IOException {
        System.out.println(serverCertificate.getCertificateList()[0].getSubject());

        if (this.serverCertList == null) {
            final org.bouncycastle.asn1.x509.Certificate[] serverCertList = serverCertificate.getCertificateList();
            if (serverCertList != null) {
                this.serverCertList = new ArrayList<Certificate>();
                try {
                    final CertificateFactory x509cf = CertificateFactory.getInstance("X509");
                    for (final org.bouncycastle.asn1.x509.Certificate cert : serverCertList) {
                        this.serverCertList
                                .add(x509cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded())));
                    }
                } catch (final CertificateException e) {
                    throw new IOException(e);
                }
            }
        }
    }

    /**
     * Returns the current available list of <em>server certificates</em>.
     *
     * @return Returns a List of the current available
     *         <em>server certificates</em>.
     */
    final List<Certificate> getServerCertList() {
        return this.serverCertList;
    }


    @Override
    public TlsCredentials getClientCredentials(final CertificateRequest certificateRequest) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}

