package org.poem.urule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.bstek.urule.Utils;
import com.bstek.urule.action.ActionValue;
import com.bstek.urule.runtime.KnowledgePackage;
import com.bstek.urule.runtime.KnowledgeSession;
import com.bstek.urule.runtime.KnowledgeSessionFactory;
import com.bstek.urule.runtime.response.RuleExecutionResponse;
import com.bstek.urule.runtime.service.KnowledgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.poem.entity.Customer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bstek.urule.runtime.EnumTarget.JSON_VALUES;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TargetKnowledgeServiceTest {

    @Test
    public void doTest() throws Exception {

        //从Spring中获取KnowledgeService接口实例
        KnowledgeService service = (KnowledgeService) Utils.getApplicationContext().getBean(KnowledgeService.BEAN_ID);
        //通过KnowledgeService接口获取指定的资源包"test123"
        KnowledgePackage knowledgePackage = service.getKnowledge("project/target-test-package");
        //通过取到的KnowledgePackage对象创建KnowledgeSession对象
        KnowledgeSession session = KnowledgeSessionFactory.newKnowledgeSession(knowledgePackage);

        Map<String, Object> parameter = new HashMap<String, Object>();
        //触发规则时并设置参数
        List<Customer> customers = EntityList.getEntity();
        JSONObject dest = EntityList.convertToJson(customers);
        Map<String, Boolean> resultMAp = new HashMap<>();
        for (String s : dest.keySet()) {
            JSONObject o = dest.getJSONObject(s);
            JSONArray jsonValues = o.getJSONArray(JSON_VALUES);
            Object[] lists = jsonValues.toArray(new Object[jsonValues.size()]);
            parameter.put("List", Arrays.asList(lists));
            RuleExecutionResponse ruleExecutionResponse =  session.fireRules(parameter);
            for (ActionValue actionValue : ruleExecutionResponse.getActionValues()) {
                Object va = actionValue.getValue();
                if (va instanceof Boolean){
                    resultMAp.put(s, Boolean.valueOf(String.valueOf(va)));
                }
            }
        }
        System.out.println(JSONObject.toJSONString(resultMAp));
    }

}
