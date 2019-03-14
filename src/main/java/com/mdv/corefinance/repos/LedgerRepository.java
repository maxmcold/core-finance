package com.mdv.corefinance.repos;

import java.util.List;


import com.mdv.corefinance.beans.Ledger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface LedgerRepository extends MongoRepository<Ledger, ObjectId> {


    List<Ledger> findLedgerBySource(String source);

    List<Ledger> findLedgerByTarget(String target);

    @Override
    List<Ledger> findAll();
}
