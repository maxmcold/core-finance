package com.mdv.corefinance.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "subscriber")
public class Subscriber {

    @Id
    public ObjectId id;

    public String rate;
    public String type;
    public String productId;

    public List<Account> accounts;

}
