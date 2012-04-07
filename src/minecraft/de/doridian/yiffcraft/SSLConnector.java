package de.doridian.yiffcraft;

import net.minecraft.client.Minecraft;
import sun.security.x509.*;

import javax.net.ssl.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Date;

public class SSLConnector {
	public static SSLSocketFactory allTrustingSocketFactory;

	public static void init() {
		try {
			char[] keyPW = Yiffcraft.licenseKey.toCharArray();

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {

					}
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {

					}
				}
			};

			KeyStore kStore  = KeyStore.getInstance(KeyStore.getDefaultType());
			File keyStoreFile = new File(Minecraft.getMinecraftDir(), "client.keystore");
			if(!keyStoreFile.exists()) {
				fillKeyStore(kStore, keyPW, keyStoreFile);
			} else {
				try {
					kStore.load(new FileInputStream(keyStoreFile), keyPW);
					if(!kStore.containsAlias("ycclientauth")) throw new Exception("Key missing!");
				} catch(Exception e) {
					e.printStackTrace();
					fillKeyStore(kStore, keyPW, keyStoreFile);
				}
			}

			KeyManagerFactory kmfac = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfac.init(kStore, keyPW);

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(kmfac.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
			allTrustingSocketFactory = sc.getSocketFactory();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	static void fillKeyStore(KeyStore kStore, char[] keyPW, File keyStoreFile) throws Exception
	{
		keyStoreFile.delete();
		
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
		kpGen.initialize(1024);
		KeyPair keyPair = kpGen.generateKeyPair();

		X509Certificate cert = generateCertificate("CN=ycclientauth", keyPair, 3650, "SHA1withRSA");

		kStore.load(null, null);
		kStore.setKeyEntry("ycclientauth", keyPair.getPrivate(), keyPW, new java.security.cert.Certificate[] { cert });
		kStore.store(new FileOutputStream(keyStoreFile), keyPW);
	}

	static X509Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException, IOException
	{
	  PrivateKey privkey = pair.getPrivate();
	  X509CertInfo info = new X509CertInfo();
	  Date from = new Date();
	  Date to = new Date(from.getTime() + days * 86400000l);
	  CertificateValidity interval = new CertificateValidity(from, to);
	  BigInteger sn = new BigInteger(64, new SecureRandom());
	  X500Name owner = new X500Name(dn);

	  info.set(X509CertInfo.VALIDITY, interval);
	  info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
	  info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
	  info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
	  info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
	  info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
	  AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
	  info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

	  // Sign the cert to identify the algorithm that's used.
	  X509CertImpl cert = new X509CertImpl(info);
	  cert.sign(privkey, algorithm);

	  // Update the algorith, and resign.
	  algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
	  info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
	  cert = new X509CertImpl(info);
	  cert.sign(privkey, algorithm);
	  return cert;
	}
}
