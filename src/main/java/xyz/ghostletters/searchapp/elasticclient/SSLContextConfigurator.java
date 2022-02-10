package xyz.ghostletters.searchapp.elasticclient;

import io.quarkus.elasticsearch.restclient.lowlevel.ElasticsearchClientConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClientBuilder;

import javax.enterprise.context.Dependent;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@ElasticsearchClientConfig
public class SSLContextConfigurator implements RestClientBuilder.HttpClientConfigCallback {
//    @Override
//    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//        try {
//            String keyStorePass = "changeme";
//            Path trustStorePath = Paths.get("/home/newur/http.p12");
//            KeyStore truststore = KeyStore.getInstance("pkcs12");
//            try (InputStream is = Files.newInputStream(trustStorePath)) {
//                truststore.load(is, keyStorePass.toCharArray());
//            }
//            SSLContextBuilder sslBuilder = SSLContexts.custom()
//                    .loadTrustMaterial(truststore, null);
//            SSLContext sslContext = sslBuilder.build();
//            httpClientBuilder.setSSLContext(sslContext);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return httpClientBuilder;
//    }

    @Override
    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
        try {
            Path caCertificatePath = Paths.get("/home/newur/http_ca.crt");
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate trustedCa;
            try (InputStream is = Files.newInputStream(caCertificatePath)) {
                trustedCa = factory.generateCertificate(is);
            }
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null);
            final SSLContext sslContext = sslContextBuilder.build();
            httpClientBuilder.setSSLContext(sslContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return httpClientBuilder;
    }
}
