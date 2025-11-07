package com.cardgame.cardserver.util;
import java.util.LinkedHashMap;
public class ApiResponse extends LinkedHashMap<String,Object> {
    public static ApiResponse of(String k,Object v){ ApiResponse r=new ApiResponse(); r.put(k,v); return r; }
    public static ApiResponse of(Object... kv){
        ApiResponse r=new ApiResponse();
        if(kv!=null){
            if((kv.length&1)==1) throw new IllegalArgumentException("even number of args required");
            for(int i=0;i<kv.length;i+=2){ r.put((String)kv[i], kv[i+1]); }
        }
        return r;
    }
    public ApiResponse putKV(String k,Object v){ super.put(k,v); return this; }
    public ApiResponse ok(){ put("ok", true); return this; }
    public ApiResponse detail(Object o){ put("detail", o); return this; }
}
