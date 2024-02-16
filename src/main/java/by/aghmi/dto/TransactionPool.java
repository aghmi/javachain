package by.aghmi.dto;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionPool {

    private final List<Transaction> transactionPool;

    public TransactionPool() {
        this.transactionPool = new LinkedList<>();
    }

    public void addTransaction(final Transaction transaction) {
        if (transaction != null && !transactionPool.contains(transaction)) {
            transactionPool.add(transaction);
            log.info("Transaction added to memory pool. {}", transaction);
        }
    }

    public List<Transaction> getTransactionPool() {
        final List<Transaction> transactionPool = new LinkedList<>(this.transactionPool);
        this.transactionPool.clear();
        return transactionPool;
    }
}
