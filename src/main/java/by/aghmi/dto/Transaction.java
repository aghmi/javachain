package by.aghmi.dto;

import static java.security.Signature.getInstance;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Transaction {

    private PublicKey sender;
    private PublicKey recipient;
    private Double amount;
    private byte[] signature;

    public Transaction(final PublicKey sender, final PublicKey recipient, final Double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public void generateSignature(final PrivateKey privateKey) {
        signature = signData(signatureData(), privateKey);
        log.info("Signature generated.");
    }

    private byte[] signData(final byte[] data, final PrivateKey privateKey) {
        try {
            final Signature signer = getInstance("SHA256withECDSA", "BC");
            signer.initSign(privateKey);
            signer.update(data);

            return signer.sign();
        } catch (Exception e) {
            throw new RuntimeException("Error sighing data.", e);
        }
    }

    public boolean verifySignature() {
        return verifySignature(signatureData(), signature, sender);

    }

    private boolean verifySignature(final byte[] data, final byte[] signature, final PublicKey publicKey) {
        try {
            final Signature verifier = getInstance("SHA256withECDSA", "BC");
            verifier.initVerify(publicKey);
            verifier.update(data);
            return verifier.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying the signature.", e);
        }
    }

    @Override
    public String toString() {
        return "SENDER %s -> RECIPIENT %s: -> AMOUNT: %s$JVC".formatted(sender, recipient, amount);
    }

    private byte[] signatureData() {
        return (sender.toString() + recipient.toString() + amount).getBytes();
    }
}
