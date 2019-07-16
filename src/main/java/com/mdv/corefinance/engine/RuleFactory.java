package com.mdv.corefinance.engine;

import com.mdv.corefinance.beans.Product;
import com.mdv.corefinance.controllers.LoanRestController;
import com.mdv.corefinance.exceptions.GenericRestException;
import com.mdv.corefinance.repos.ProductRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;




@Component
public class RuleFactory {
    private static final Logger logger = LoggerFactory.getLogger(LoanRestController.class);


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FixedRateRuleImpl fixedRateRule;

    @Autowired
    private  FeeWithLatePaymentRuleImpl feeWithLatePaymentRule;

    public RuleFactory(){

    }

    public Rule getRule(String pid){

        try {
            ObjectId oid = new ObjectId(pid);
            Product product = productRepository.findProductById(oid);
            if (isFeeWithLatePayment(product))
                return feeWithLatePaymentRule;

            if (isFixedRate(product))
                return fixedRateRule;


        }catch (RuntimeException e){

            //TODO: improve the error management
            logger.debug("Error evaluating rule "+e.getMessage());
            throw new GenericRestException("Error evaluating rule "+e.getMessage());
        }
        return null;

    }

    private boolean isFeeWithLatePayment(Product product){

        //TODO: remove hardcoded value
        if (product.type.equals("2")) return true;
        return false;

    }
    private boolean isFixedRate(Product product){

        //TODO: remove hardcoded value
        if (product.type.equals("1")) return true;
        return false;

    }








}
