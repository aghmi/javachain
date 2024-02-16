package by.aghmi;

import static java.lang.System.currentTimeMillis;
import static java.time.Instant.now;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import by.aghmi.dto.Block;
import by.aghmi.dto.Blockchain;
import by.aghmi.dto.Transaction;
import by.aghmi.dto.TransactionPool;
import by.aghmi.utils.MiningTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockchainDemo {

    private static final String ECDSA_ALGORITHM = "ECDSA";
    private static final String BOUNCY_CASTLE_PROVIDER = "BC";
    private static final int KEY_SIZE = 256;
    private static final int AWAIT_TERMINATION_TIMEOUT = 60;
    private static final int MINING_DIFFICULTY = 5;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        final Blockchain blockchain = initializeBlockchain();
        final TransactionPool pool = new TransactionPool();
        final AtomicBoolean found = new AtomicBoolean(false);
        final KeyPairGenerator keyGen = getKeyPairGenerator();

        final int blockchainLength = getUserInput("Preferred blockchain length: ");
        final int transactionsPerBlock = getUserInput("Preferred transactions per block: ");

        for (int i = 0; i < blockchainLength; i++) {
            generateTransactions(pool, keyGen, transactionsPerBlock);
            mineBlocks(blockchain, pool, found);
        }
        log.info("Blockchain end?");
    }

    private static void generateTransactions(final TransactionPool pool, final KeyPairGenerator keyGen,
            final int transactionsPerBlock) {
        for (int i = 0; i < transactionsPerBlock; i++) {
            final Transaction transaction = createTransaction(keyGen);
            if (transaction != null) {
                log.info("Transaction signature verified.");
                pool.addTransaction(transaction);
            }
        }
    }

    private static Transaction createTransaction(final KeyPairGenerator keyGen) {
        final KeyPair senderKeyPair = keyGen.generateKeyPair();
        final KeyPair recipientKeyPair = keyGen.generateKeyPair();
        final Double amount = 1 + (100 * Math.random());
        final Transaction transaction =
                new Transaction(senderKeyPair.getPublic(), recipientKeyPair.getPublic(), amount);
        transaction.generateSignature(senderKeyPair.getPrivate());
        if (transaction.verifySignature()) {
            log.info("new tx at " + now());
            return transaction;
        }
        return null;
    }

    private static void mineBlocks(final Blockchain blockchain, final TransactionPool pool, final AtomicBoolean found) {
        final long startTime = currentTimeMillis();
        log.info("Mining begins: " + now());
        while (!pool.getTransactionPool().isEmpty()) {
            final List<Transaction> transactionsForBlock = pool.getTransactionPool();
            final Block currentBlock = prepareBlock(blockchain, transactionsForBlock);
            if (mineBlock(currentBlock, found)) {
                blockchain.addBlock(currentBlock);
                final long elapsedTime = (currentTimeMillis() - startTime) / 1000;
                log.info("Block mined, successfully and added to the blockchain in " + elapsedTime + " seconds.");
            } else {
                log.info("Mining did not succeed in time " + AWAIT_TERMINATION_TIMEOUT + " or was interrupted");
            }
            found.set(false);
        }
    }

    private static Block prepareBlock(final Blockchain blockchain, final List<Transaction> transactionsForBlock) {
        final String prevHash = blockchain.getLastBlock() != null ? blockchain.getLastBlock().getCurrentHash() : "0";
        return new Block(blockchain.getBlockchain().size(), transactionsForBlock, prevHash);
    }

    private static boolean mineBlock(final Block currentBlock, final AtomicBoolean found) {
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            for (int j = 0; j < Runtime.getRuntime().availableProcessors(); j++) {
                executor.submit(new MiningTask(currentBlock, MINING_DIFFICULTY, found));
            }
            executor.shutdown();
            return executor.awaitTermination(AWAIT_TERMINATION_TIMEOUT, SECONDS) && found.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            executor.shutdownNow();
        }
    }

    @SneakyThrows
    private static KeyPairGenerator getKeyPairGenerator() {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ECDSA_ALGORITHM, BOUNCY_CASTLE_PROVIDER);
        keyGen.initialize(KEY_SIZE, new SecureRandom());
        return keyGen;
    }

    private static Blockchain initializeBlockchain() {
        return new Blockchain();
    }

    private static int getUserInput(final String prompt) {
        final Scanner scan = new Scanner(System.in);
        System.out.print(prompt);
        return scan.nextInt();
    }
}
