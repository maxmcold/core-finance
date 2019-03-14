package com.mdv.corefinance.repos;



import com.mdv.corefinance.beans.Transaction;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, ObjectId> {

    @Override
    List<Transaction> findAll();




}
