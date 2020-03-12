package com.example.narendra.eatfoodserver.Model;

import java.util.List;

/**
 * Created by narendra on 3/13/2018.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;

}
