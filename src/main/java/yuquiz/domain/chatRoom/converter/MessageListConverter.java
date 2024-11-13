package yuquiz.domain.chatRoom.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import yuquiz.domain.chatRoom.dto.Message;

import java.util.List;

@Converter
public class MessageListConverter implements AttributeConverter<List<Message>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Message> messageList) {
        try {
            return objectMapper.writeValueAsString(messageList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Message.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}