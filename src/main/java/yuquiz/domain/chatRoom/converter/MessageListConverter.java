package yuquiz.domain.chatRoom.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import yuquiz.domain.chatRoom.dto.MessageReq;

import java.util.List;

@Converter
public class MessageReqListConverter implements AttributeConverter<List<MessageReq>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<MessageReq> messageReqList) {
        try {
            return objectMapper.writeValueAsString(messageReqList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<MessageReq> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MessageReq.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}