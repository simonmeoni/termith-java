package org.atilf.models.enrichment;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * normalize treetagger tags during json serialization in english or french
 * @author Simon Meoni
 *         Created on 08/09/16.
 */
public class TagNormalizer {

    private  Map<String,String> _ttTag;
    private final Logger _LOGGER = LoggerFactory.getLogger(TagNormalizer.class);


    public TagNormalizer(String resourcePath) {
        _ttTag = parseResource(resourcePath);

    }

    private Map<String,String> parseResource(String resourcePath) {
        Map<String,String> result = new HashMap<>();
        boolean inTag = false;
        try {
            JsonFactory jFactory = new JsonFactory();
            JsonParser jParser = jFactory.createParser(getClass().getClassLoader().getResourceAsStream(resourcePath));
            while (jParser.nextToken() != null) {
                if (jParser.getCurrentName() == "tag") {
                    inTag = true;
                }
                else if (inTag && jParser.getCurrentToken() == JsonToken.END_OBJECT) {
                    break;
                }
                else if (inTag){
                    result.put(jParser.getCurrentName(),jParser.getValueAsString());
                }
            }

            jParser.close();
            if (result.size() == 0){
                throw new IOException("cannot parse json file : file not found");
            }

        }
        catch (IOException e) {
            _LOGGER.error("cannot parse json file",e);
        }
        return result;
    }

    public String normalize(String token) {
        return _ttTag.get(token);
    }
}
