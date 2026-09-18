package com.vts.vtsapproot.Tools;

import android.annotation.SuppressLint;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MultiDateFormatAdapter
        extends TypeAdapter<Date> {

    // Liệt kê các định dạng mà SQL của bạn có thể trả về
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat[] formats = new SimpleDateFormat[] {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), // index 0: ISO format
            new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss"), // index 1
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),    // index 2
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),    // index 3
            new SimpleDateFormat("yyyy-MM-dd"),             // index 4
            new SimpleDateFormat("yyyy/MM/dd")              // index 5
    };

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Kiểm tra nếu ngày không có phần giờ, phút, giây (00:00:00)
            Calendar cal = Calendar.getInstance();
            cal.setTime(value);
            if (cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0 && cal.get(Calendar.SECOND) == 0) {
                // Gửi yyyy-MM-dd
                out.value(formats[4].format(value));
            } else {
                // Gửi định dạng yyyy-MM-dd'T'HH:mm:ss lên API
                out.value(formats[0].format(value));
            }
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String dateStr = in.nextString();
        // Lặp qua danh sách định dạng để parse thử
        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(dateStr);
            } catch (ParseException e) {
                // Thất bại thì bỏ qua, thử định dạng tiếp theo
            }
        }

        // Nếu không khớp với bất kỳ định dạng nào
        throw new IOException("Không thể parse ngày tháng: " + dateStr);
    }
}