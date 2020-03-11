package org.borisenko.maxim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties (ignoreUnknown = true)
public class TaskResponse {
      Long id;
      Long project_id;
      Long section_id;
      Long parent;
      Integer order;
      String content;
      Boolean completed;
      List<Integer> label_ids;
      Integer priority;
      Integer comment_count;
      LocalDateTime created;
      String url;
      Due due;

      @Data
      @JsonIgnoreProperties (ignoreUnknown = true)
      class Due{
          Boolean recurring;
          String string;
          LocalDateTime datetime;
          String timezone;
          LocalDate date;

    }

}
