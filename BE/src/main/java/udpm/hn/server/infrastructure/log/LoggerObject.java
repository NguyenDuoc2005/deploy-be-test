package udpm.hn.server.infrastructure.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoggerObject implements Serializable {

    private int orderNumber;

    private String content;

    private String IP;

    private String mail;

    private String method;

    private String createDate;

    private String author;

    private String status;

    private String pathFile;

    private String request;

    private String api;

    private String param;

}
