package com.mdv.corefinance.repos;



import com.mdv.corefinance.beans.Transaction;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, ObjectId> {

    @Override
    List<Transaction> findAll();


    List<Transaction> findTransactionsByFromOrTo(String from, String to, Sort sort);

    List<Transaction> findTransactionsByFromOrTo(String from, String to);




}
