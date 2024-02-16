package by.aghmi.utils;

import java.util.concurrent.atomic.AtomicBoolean;
import by.aghmi.dto.Block;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MiningTask implements Runnable {

    private final Block block;
    private final int prefixLength;
    private final AtomicBoolean found;

    @Override
    public void run() {
        String hash;
        long nonce = 0;
        while (!found.get() && !Thread.currentThread().isInterrupted()) {
            nonce++;
            hash = BlockchainUtils.calculateBlockHash(block, nonce);
            if (hash.startsWith("0".repeat(prefixLength))) {
                found.set(true);
                block.setNonce(nonce);
                block.setCurrentHash(hash);
                System.out.println(
                        "Block mined by " + Thread.currentThread().getName() + " with hash: " + hash + ", with nonce: "
                                + nonce);
                break;
            }
        }
    }
}
