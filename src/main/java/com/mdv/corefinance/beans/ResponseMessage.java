package com.mdv.corefinance.beans;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class ResponseMessage {

    @Id
    public ObjectId id;
    public String message;
    public String errorDetails;
    public String errorCode;
    public String errorDescription;

    public Object out;

}
