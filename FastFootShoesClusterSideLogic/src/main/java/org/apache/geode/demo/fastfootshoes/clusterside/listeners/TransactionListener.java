package org.apache.geode.demo.fastfootshoes.clusterside.listeners;

import java.util.Optional;
import java.util.Properties;

import org.apache.geode.demo.fastfootshoes.clusterside.util.ReferenceHelper;
import org.apache.geode.demo.fastfootshoes.model.Product;
import org.apache.geode.demo.fastfootshoes.model.Transaction;
import org.apache.geode.demo.fastfootshoes.repositories.ProductRepository;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

/**
 * This listener will be called when the Transaction region is updated.
 * It is used to update Product stock based on the Transactions being placed
 * NOTE: This is not best practice:
 * http://gemfire81.docs.pivotal.io/latest/userguide/index.html#developing/events/writing_callbacks_that_modify_the_cache.html#writing_callbacks_that_modify_the_cache
 *
 * @author lshannon
 */
@Component
public class TransactionListener extends CacheListenerAdapter<String, Object> implements Declarable {

    @Autowired
    private ProductRepository productRepository;

    public void init(Properties props) {
    }

    /**
     * After an entry is create in the Transaction region, the Product in the Transaction will
     * have its stock updated to take into account the quantity in the Transaction
     * NOTE: http://gemfire81.docs.pivotal.io/latest/userguide/index.html#developing/events/writing_callbacks_that_modify_the_cache.html#writing_callbacks_that_modify_the_cache
     */
    @Override
    public void afterCreate(EntryEvent<String, Object> entryEvent) {
        updateProductRegion(entryEvent);
    }

    /**
     * After an entry is updated in the Transaction region, the Product in the Transaction will
     * have its stock updated to take into account the quantity in the Transaction
     * NOTE: This is not best practice, and is for demonstrative purposes only.
     */
    @Override
    public void afterUpdate(EntryEvent<String, Object> entryEvent) {
        updateProductRegion(entryEvent);
    }

    private void updateProductRegion(EntryEvent<String, Object> entryEvent) {
        Transaction transaction = null;
        if (entryEvent.getNewValue() instanceof PdxInstanceImpl) {
            transaction = ReferenceHelper.toObject(entryEvent.getNewValue(), Transaction.class);
        } else {
            transaction = (Transaction) entryEvent.getNewValue();
        }
        if (transaction.getOrderStatus().equals(Transaction.ORDER_OPEN)) {
            //get the product
            Product product = resolveReference(productRepository.findById(transaction.getProductId()));
            //update the stock - NOTE: risk of inconsistency here
            product.setStockOnHand(product.getStockOnHand() - transaction.getQuantity());
            //save the product
            System.out.println("Updating product " + product.getId());
            productRepository.save(product);
        }
    }

    /*
     * If Read Serialized is set to true, the cluster will return PdxInstanceImpl. These will need to be cast
     */
    private Product resolveReference(Object obj) {
        if (obj instanceof PdxInstanceImpl) {
            return ReferenceHelper.toObject(obj, Product.class);
        } else if (obj instanceof Optional) {
            Optional op = (Optional) obj;
            if (op.isPresent()) {
                obj = op.get();
                if (obj instanceof Product) {
                    return (Product) obj;
                }
            }
        } else if (obj instanceof Product) {
            return (Product) obj;
        }
        return null;
    }

}
