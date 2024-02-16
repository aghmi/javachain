package by.aghmi.dto;

import static by.aghmi.utils.BlockchainUtils.generateGenesisBlock;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
public class Blockchain {

    private final List<Block> blockchain;

    public Blockchain() {
        blockchain = new LinkedList<>();
        blockchain.add(generateGenesisBlock());
        log.info("Genesis-block created!");
    }

    public void addBlock(final Block block) {
        if (!blockchain.isEmpty()) {
            final Block lastBlock = getLastBlock();
            if (block.getIndex() == lastBlock.getIndex() + 1) {
                if (block.getPrevHash().equals(lastBlock.getCurrentHash())) {
                    blockchain.add(block);
                    log.warn("Block #{} is successfully stored into blockchain: {}", block.getIndex(),
                            block.getCurrentHash());
                } else {
                    throw new IllegalArgumentException("Invalid block: Previous hash does not match.");
                }
            } else {
                throw new IllegalArgumentException("Invalid block: Index is not sequential.");
            }
        }
    }

    public Block getLastBlock() {
        return !blockchain.isEmpty() ? blockchain.get(blockchain.size() - 1) : null;
    }
}
