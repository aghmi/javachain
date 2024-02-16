package by.aghmi.utils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.MessageDigest.getInstance;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import by.aghmi.dto.Block;
import by.aghmi.dto.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockchainUtils {

    public static String calculateBlockHash(final Block block, final long nonce) {
        final MessageDigest messageDigest;
        byte[] bytes = null;
        try {
            messageDigest = getInstance("SHA-256");
            bytes = messageDigest.digest(getDataToHash(block, nonce));
        } catch (final NoSuchAlgorithmException ex) {
            log.error("Error calculating block hash", ex);
        }
        final StringBuilder buffer = new StringBuilder();
        for (final byte b : requireNonNull(bytes)) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public static Block generateGenesisBlock() {
        return new Block(0, emptyList(), "0");
    }

    private static byte[] getDataToHash(final Block block, final long nonce) {
        final String dataToHashBuilder =
                block.getPrevHash() + block.getTimestamp() + block.getTransactions().stream().map(Transaction::toString)
                        .collect(joining()) + nonce;
        return dataToHashBuilder.getBytes(UTF_8);
    }
}
