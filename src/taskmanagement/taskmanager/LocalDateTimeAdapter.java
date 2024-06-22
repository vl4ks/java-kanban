package taskmanagement.taskmanager;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    //сериализация
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime != null) {
            jsonWriter.value(localDateTime.format(formatter));
        } else {
            jsonWriter.nullValue();
        }

    }

    //десериализация
    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        final String text = jsonReader.nextString();
        if (text.equals("null")) {
            return null;
        } else {
            return LocalDateTime.parse(text, formatter);
        }
    }
}
