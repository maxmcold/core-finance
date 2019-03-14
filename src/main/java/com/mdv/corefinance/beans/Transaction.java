package com.mdv.corefinance.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ledger")
public class Transaction {


    @Id
    public ObjectId id;

    @Field("source")
    public String from;
    @Field("target")
    public String to;

    public String amount;

    @Field("ttype")
    public String type;

    public String currency;

    public String timestamp;

}
