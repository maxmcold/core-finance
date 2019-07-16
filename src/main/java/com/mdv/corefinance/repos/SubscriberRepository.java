package com.mdv.corefinance.repos;


import com.mdv.corefinance.beans.Subscriber;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubscriberRepository extends MongoRepository<Subscriber, ObjectId> {
    @Override
    List<Subscriber> findAll();

    Subscriber findSubscriberById(ObjectId id);

    Subscriber findSubscriberByMsisdnAndType(String msisdn, String type);

    Subscriber findSubscriberByType(String type);


}
