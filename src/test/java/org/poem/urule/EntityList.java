package org.poem.urule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.poem.entity.Customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bstek.urule.runtime.EnumTarget.JSON_TARGET;
import static com.bstek.urule.runtime.EnumTarget.JSON_VALUES;


public class EntityList {

    /**
     * 获取数据
     *
     * @return
     */
    public static List<Customer> getEntity() {
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Customer employee = new Customer();
            employee.setAge(i);
            employee.setBirthday(new Date());
            employee.setCar(i % 3 == 0);
            employee.setGender(i % 4 == 0);
            employee.setLevel(i % 2);
            employee.setMobile((15700000000L - i )+"");
            employee.setName("name" + i);
            customers.add(employee);
        }
        return customers;
    }

    /**
     * 转换成json
     * @param list
     * @return
     */
    public static JSONObject convertToJson(List<?> list){
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.toJSONString(list));
        JSONObject object, returnObject = new JSONObject();
        //沾化成数组对象
        for (Object o : jsonArray) {
            object = JSONObject.parseObject(JSONObject.toJSONString(o));
            for (String key : object.keySet()) {
                JSONObject jsonObject = returnObject.getJSONObject(key);
                if (jsonObject == null) {
                    jsonObject = new JSONObject();
                }
                JSONArray array = jsonObject.getJSONArray(JSON_VALUES);
                jsonObject.put(JSON_TARGET, Boolean.FALSE);
                if (array == null) {
                    array = new JSONArray();
                }
                array.add(object.get(key));
                jsonObject.put(JSON_VALUES, array);

                returnObject.put(key, jsonObject);
            }
        }
        return returnObject;
    }
}
