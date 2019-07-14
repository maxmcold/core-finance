package com.mdv.corefinance.engine;

import com.mdv.corefinance.beans.Product;
import com.mdv.corefinance.beans.Subscriber;
import com.mdv.corefinance.beans.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface Rule {

    public Double getFee(Double amount);
    public Transaction recover(String sid,
                                     String amount,
                                     String currency);


    public Transaction transfer(String from, String to,
                                String amount, String type,
                                String currency, Boolean forceTransfer);
    public Transaction credit(String amount, String sid,
                                    String currency);

    public Transaction allow(String sid,
                            String amount,
                              String currency);
}
