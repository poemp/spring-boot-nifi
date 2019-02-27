package org.poem.processors;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.processor.util.StandardValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 曹莉
 */
@SideEffectFree
@Tags({"JSON", "NIFI ROCKS","POEM"})
@CapabilityDescription("Fetch value from json path")
public class JsonProcessor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JsonProcessor.class);


    private List<PropertyDescriptor> propertyDescriptors;

    private Set<Relationship> relationships;

    public static final String MATCH_ATTR = "match";

    public static final PropertyDescriptor JSON_PATH = new PropertyDescriptor.Builder()
            .name("JSON PATH")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("SUCCESS")
            .description("success relationship")
            .build();

    /**
     * 初始化
     *
     * @param context
     */
    @Override
    public void init(final ProcessorInitializationContext context) {
        List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();
        propertyDescriptors.add(JSON_PATH);
        this.propertyDescriptors = Collections.unmodifiableList(propertyDescriptors);

        Set<Relationship> relationships = new HashSet<>();
        relationships.add(SUCCESS);
        this.relationships = Collections.unmodifiableSet(relationships);
        logger.info("init json processor");
    }

    @Override
    public Set<Relationship> getRelationships() {
        return relationships;
    }

    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return propertyDescriptors;
    }

    @Override
    public void onTrigger(ProcessContext processContext, ProcessSession processSession) throws ProcessException {
        final AtomicReference<String> value = new AtomicReference<>();

        FlowFile flowfile = processSession.get();

        processSession.read(flowfile, new InputStreamCallback() {
            @Override
            public void process(InputStream in) throws IOException {
                try {
                    String json = IOUtils.toString(in);
                    logger.info("read json :" + json);
                    String result = JsonPath.read(json, "$.hello");
                    logger.info("get json :" + result);
                    value.set(result);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    getLogger().error("Failed to read json string.");
                }
            }
        });

        // Write the results to an attribute
        String results = value.get();
        if(results != null && !results.isEmpty()){
            flowfile = processSession.putAttribute(flowfile, "match", results);
        }

        // To write the results back out ot flow file
        flowfile = processSession.write(flowfile, new OutputStreamCallback() {

            @Override
            public void process(OutputStream out) throws IOException {
                out.write(value.get().getBytes());
            }
        });

        processSession.transfer(flowfile, SUCCESS);

    }
}
