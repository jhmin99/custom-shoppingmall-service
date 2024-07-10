package jihong99.shoppingmall.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
    /**
     * Convert an object to a JSON string.
     *
     * @param obj The object to be converted.
     * @return The JSON string representing the object.
     * @throws JsonProcessingException If an error occurs during JSON processing.
     */
    public static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper.writeValueAsString(obj);
    }
}
