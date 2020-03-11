package org.borisenko.maxim.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TaskRequest {
    String content;
    Long project_id;
    Integer section_id;
    Long parent;
    Integer order;
    List<Integer> label_ids;
    Integer priority;
    String due_string;
    String due_date;
    String due_datetime;
    String due_lang;
}
