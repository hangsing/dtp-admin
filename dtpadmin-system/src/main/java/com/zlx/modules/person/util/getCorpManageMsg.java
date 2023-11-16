package com.zlx.modules.person.util;

import java.util.HashMap;
import java.util.Map;

public class getCorpManageMsg {


    public static String evaluate (String index1, String pre_index1, String pre_pre_index1,String msg1,
                                   String index2, String pre_index2, String pre_pre_index2,String msg2,
                                   String index3, String pre_index3, String pre_pre_index3,String msg3,
                                   String index4, String pre_index4, String pre_pre_index4,String msg4,
                                   String index5, String pre_index5, String pre_pre_index5,String msg5) {
        Map<String, String> mp1 = CommonUtil.getMsg(index1, pre_index1, pre_pre_index1, msg1);
        Map<String, String> mp2 = CommonUtil.getMsg(index2, pre_index2, pre_pre_index2, msg2);
        Map<String, String> mp3 = CommonUtil.getMsg(index3, pre_index3, pre_pre_index3, msg3);
        Map<String, String> mp4 = CommonUtil.getMsg(index4, pre_index4, pre_pre_index4, msg4);
        Map<String, String> mp5 = CommonUtil.getMsg(index5, pre_index5, pre_pre_index5, msg5);

        Map<String, String> initMap = new HashMap<String,String>();

        Map<String, String> execMap1 = CommonUtil.putMsg(initMap, mp1);
        Map<String, String> execMap2 = CommonUtil.putMsg(execMap1, mp2);
        Map<String, String> execMap3 = CommonUtil.putMsg(execMap2, mp3);
        Map<String, String> execMap4 = CommonUtil.putMsg(execMap3, mp4);
        Map<String, String> resultMap = CommonUtil.putMsg(execMap4, mp5);
        String result = "在企业经营方面";
        if(resultMap.size() == 1 && resultMap.containsKey("-1")){
            return "";
        }else {
            if(resultMap.containsKey("1")) {
                result = CommonUtil.concatResult(result) + resultMap.get("1")+"逐年增加";
            }

            if(resultMap.containsKey("2")) {
                result = CommonUtil.concatResult(result) + resultMap.get("2")+"逐年减少";
            }

            if(resultMap.containsKey("3") && resultMap.containsKey("4")) {
                result = CommonUtil.concatResult(result) + resultMap.get("3")+"、"+resultMap.get("4")+
                        "呈波动趋势，其中"+resultMap.get("4")+"波动较大,请关注";
            }else if(!resultMap.containsKey("3") && resultMap.containsKey("4")){
                result = CommonUtil.concatResult(result) + resultMap.get("4")+
                        "呈波动趋势，其中"+resultMap.get("4")+"波动较大,请关注";
            }else if(resultMap.containsKey("3") && !resultMap.containsKey("4")){
                result = CommonUtil.concatResult(result) + resultMap.get("3")+
                        "呈波动趋势";
            }
            return result+"；";
        }
    }
}
