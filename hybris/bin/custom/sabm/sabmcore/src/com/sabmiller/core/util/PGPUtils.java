package com.sabmiller.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

import de.hybris.platform.core.Registry;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;

public class PGPUtils {

    public static PGPPublicKey readPublicKey(final String fileName) throws IOException, PGPException
    {
    	final InputStream undecodedStream = Registry.getApplicationContext().getResource(fileName).getInputStream();

		final InputStream inputStream =  org.bouncycastle.openpgp.PGPUtil.getDecoderStream(undecodedStream);
		
		final PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(inputStream);
	    undecodedStream.close();
		inputStream.close();
		//
		// we just loop through the collection till we find a key suitable for encryption, in the real
		// world you would probably want to be a bit smarter about this.
		//
		PGPPublicKey publicKey = null;

		//
		// iterate through the key rings.
		//
		final Iterator<PGPPublicKeyRing> rIt = pgpPub.getKeyRings();

		while (publicKey == null && rIt.hasNext())
		{
			final PGPPublicKeyRing kRing = rIt.next();
			final Iterator<PGPPublicKey> kIt = kRing.getPublicKeys();
			while (publicKey == null && kIt.hasNext())
			{
				final PGPPublicKey key = kIt.next();
				if (key.isEncryptionKey())
				{
					publicKey = key;
				}
			}
		}

		if (publicKey == null)
		{
			throw new IllegalArgumentException("Can't find public key in the key ring.");
		}

		return publicKey;

    }




    public static void encryptFile(
        OutputStream out,
			final File file,
        final PGPPublicKey encKey,
        final boolean armor,
        final boolean withIntegrityCheck)
        throws IOException, NoSuchProviderException, PGPException
    {
        Security.addProvider(new BouncyCastleProvider());

        if (armor) {
            out = new ArmoredOutputStream(out);
        }

        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        PGPUtil.writeFileToLiteralData(
               bOut,
                PGPLiteralData.BINARY,
				file);


        final BcPGPDataEncryptorBuilder dataEncryptor = new BcPGPDataEncryptorBuilder(PGPEncryptedData.TRIPLE_DES);
        dataEncryptor.setWithIntegrityPacket(withIntegrityCheck);
        dataEncryptor.setSecureRandom(new SecureRandom());

        final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(dataEncryptor);
        encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(encKey));

        final byte[] bytes = bOut.toByteArray();
        final OutputStream cOut = encryptedDataGenerator.open(out, bytes.length);
        cOut.write(bytes);
        cOut.close();
		out.close();
	}








}