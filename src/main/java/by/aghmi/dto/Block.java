package by.aghmi.dto;

import static java.time.Instant.now;
import static by.aghmi.utils.BlockchainUtils.calculateBlockHash;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Block {

    private final Integer index;
    private final Long timestamp;
    private final List<Transaction> transactions;
    private final String prevHash;
    private String currentHash;
    private Long nonce = 0L; // number only used once - value we search during mining for PoW

    public Block(final Integer index, final List<Transaction> transactions, final String prevHash) {
        this.index = index;
        this.transactions = transactions;
        this.prevHash = prevHash;
        this.timestamp = now().toEpochMilli();
        this.currentHash = calculateBlockHash(this, nonce);
        this.nonce = 0L;
    }
}
